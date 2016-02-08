package com.suter.hawkeye;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.*;
import java.lang.Math;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class NowWindowBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(NowWindowBolt.class);
	private Map<String, MonitorPerfAgg> window;
	
	private long currentNowWindowStart;
	private long currentHistoryWindowStart;
	private Jedis jedis;
	Session casSession;
	PreparedStatement historyStmt;
	
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("monitor", "sev", "agg"));
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {
		LOG.info("NowWindowBolt.prepare: enter");
		window = new HashMap<String, MonitorPerfAgg>();
		
		currentNowWindowStart = HawkeyeUtil.getTime();
		currentHistoryWindowStart = currentNowWindowStart;
		
		jedis = new Jedis(HawkeyeUtil.nimbusHost);
		jedis.ping();

		Cluster cluster = Cluster.builder().addContactPoint(HawkeyeUtil.cassandraHost).build();
		casSession = cluster.connect(HawkeyeUtil.hawkeyeKeySpace);
		String stmt = "SELECT monitor, tdeltaagg, nevents, time_window_size_ms " + 
			"FROM monitor_history WHERE monitor = ? and record_time_year = ? limit 100";
		historyStmt = casSession.prepare(stmt);
		LOG.info("NowWindowBolt.prepare: done");
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, HawkeyeUtil.nowWindowSizeMS/1000);
		return conf;
	}
	
	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {

		if (isTickTuple(tuple)) {
			checkAndSendAlerts(outputCollector);
			persistProcWindowAggregates();
		} else {

			String monitor  = tuple.getStringByField("monitor");
			Long tsIn = tuple.getLongByField("tsIn");
			Long tsOut = tuple.getLongByField("tsOut");
			Long tDelta = tuple.getLongByField("tDelta");
			
			MonitorPerfAgg agg = getMonitorAgg(monitor);
			agg.nEvents++;
			agg.tDeltaAgg += tDelta;
			
			if (Double.isNaN(agg.min)) {
				getMonitorSummary(agg);
			}
		}
	}

	private boolean isTickTuple(Tuple tuple) {
		String sourceComponent = tuple.getSourceComponent();
		String sourceStreamId = tuple.getSourceStreamId();
		return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID)
			&& sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
	}

	private void checkAndSendAlerts(BasicOutputCollector outputCollector) {
		Set<String> monitorsAvailable = window.keySet();
		for (String monitor : monitorsAvailable) {
			MonitorPerfAgg agg = window.get(monitor);
			if (!Double.isNaN(agg.min)) {
				double through = (double)agg.tDeltaAgg/agg.nEvents;
				if ((through < agg.sig2neg) || (through > agg.sig2pos)){
					outputCollector.emit(new Values(monitor, "red", agg));
				} else if ((through < agg.sig1neg) || (through > agg.sig1pos)) {
					outputCollector.emit(new Values(monitor, "yellow", agg));
				}
			}
		}
	}
	private void persistProcWindowAggregates() {
		Long now = HawkeyeUtil.getTime();
		Set<String> monitorsAvailable = window.keySet();
		for (String monitor : monitorsAvailable) {
			MonitorPerfAgg agg = window.get(monitor);
			agg.tWindowEnd = now;
			jedis.set(monitor + HawkeyeUtil.nowJedisSuffix, new Double((double)agg.tDeltaAgg/agg.nEvents).toString());
		}
		currentNowWindowStart = now;
		window.clear();
	}

	private MonitorPerfAgg getMonitorAgg(String monitor) {
		MonitorPerfAgg agg = window.get(monitor);
		if (agg == null) {
			agg = new MonitorPerfAgg();
			agg.monitor = monitor;
			agg.tWindowStart = currentNowWindowStart;
			getMonitorSummary(agg);
			window.put(monitor, agg);
		}
		return agg;
	}
	
	private void getMonitorSummary(MonitorPerfAgg agg) {
		
		SummaryStatistics history = new SummaryStatistics();
		List<String> list = jedis.lrange(agg.monitor + HawkeyeUtil.histJedisSuffix, 0 ,-1);
		if (list.size() <= 0) {
			long now = HawkeyeUtil.getTime();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(now);
			long year = c.get(Calendar.YEAR);
		
			BoundStatement boundStatement = new BoundStatement(historyStmt);
			ResultSet results = casSession.execute(boundStatement.bind(agg.monitor, year));
		
			for (Row row : results) {
				String monitor 	= row.getString("monitor");
				Long tDeltaAgg 	= row.getLong("tdeltaagg");
				Long nEvents 	= row.getLong("nevents");
				Double through 	= (double) tDeltaAgg / nEvents;
				jedis.rpush(monitor + HawkeyeUtil.histJedisSuffix, through.toString());
			}
			list = jedis.lrange(agg.monitor + HawkeyeUtil.histJedisSuffix, 0 ,-1);
		}
		
		for(int i=0; i<list.size(); i++) {
			history.addValue(Double.parseDouble(list.get(i)));
		}
		
		double sd = history.getStandardDeviation();
		double mean = history.getMean();
		
		if (!Double.isNaN(sd)) {
			agg.min = 		history.getMin();
			agg.max = 		history.getMax();
			agg.sig2neg = 	mean - 2*sd;
			agg.sig1neg = 	mean - sd;
			agg.sig1pos = 	mean + sd;
			agg.sig2pos = 	mean + 2*sd;			
		} 
	}
	
	private void updateMonitorsSummary() {
		Set<String> monitorsAvailable = window.keySet();
		for (String monitor : monitorsAvailable) {
			MonitorPerfAgg agg = window.get(monitor);
			getMonitorSummary(agg);
		}
		currentHistoryWindowStart = HawkeyeUtil.getTime();
	}
}




