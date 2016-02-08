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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HistoryWindowBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(HistoryWindowBolt.class);
	private Map<String, MonitorPerfAgg> window;
	private long currentWindowStart;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("monitor", "agg"));
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {
		window = new HashMap<String, MonitorPerfAgg>();
		currentWindowStart = HawkeyeUtil.getTime();
		LOG.info("HistoryWindowBolt.prepare: done");
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, HawkeyeUtil.historyWindowSizeMS/1000);
		return conf;
	}
	
	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
		//LOG.info("HistoryWindowBolt.execute: 1");
		if (isTickTuple(tuple)) {
			//LOG.info("HistoryWindowBolt.execute: 2");
			emitWindowAggregates(outputCollector);
			//LOG.info("HistoryWindowBolt.execute: 3");
		} else {
			//LOG.info("HistoryWindowBolt.execute: 4");
			//LOG.info("HistoryWindowBolt.execute: tuple is: " + tuple);
			String monitor  = tuple.getStringByField("monitor");
			Long tsIn = tuple.getLongByField("tsIn");
			Long tsOut = tuple.getLongByField("tsOut");
			Long tDelta = tuple.getLongByField("tDelta");
			//LOG.info("ProcWindowBolt.execute: 5");
			
			MonitorPerfAgg agg = getMonitorAgg(monitor);
			agg.nEvents++;
			agg.tDeltaAgg += tDelta;
			//LOG.info("ProcWindowBolt.execute: 6");
		}
	}

	private boolean isTickTuple(Tuple tuple) {
		String sourceComponent = tuple.getSourceComponent();
		String sourceStreamId = tuple.getSourceStreamId();
		return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID)
			&& sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
	}
/*
	private void persistProcWindowAggregates() {
		//LOG.info("ProcWindowBolt.persistProcWindowAggregates:1");
		Long now = HawkeyeUtil.getProcWindowTime();
		Set<String> monitorsAvailable = procWindow.keySet();
		//LOG.info("ProcWindowBolt.persistProcWindowAggregates:2:");
		for (String monitor : monitorsAvailable) {
			//LOG.info("ProcWindowBolt.persistProcWindowAggregates:3: monitor=" + monitor);
			BoundStatement boundStatement = new BoundStatement(monProcWindowStmt);
			MonitorPerfAgg agg = procWindow.get(monitor);
			agg.tProcOut = now;
			//LOG.info("ProcWindowBolt.persistProcWindowAggregates:4: monitor=" + monitor);
			casSession.execute(boundStatement.bind(
				agg.monitor, agg.tsInMin, agg.tsInMax, agg.tsOutMin, agg.tsOutMax, 
				agg.tDeltaAgg, agg.nEvents, agg.tProcIn, agg.tProcOut));
			//LOG.info("ProcWindowBolt.persistProcWindowAggregates:5: monitor=" + monitor);
		}
		currentWindowStart = now;
		window.clear();
	}
*/
	private void emitWindowAggregates(BasicOutputCollector outputCollector) {
		Long now = HawkeyeUtil.getTime();
		Set<String> monitorsAvailable = window.keySet();
		for (String monitor : monitorsAvailable) {
			//to do, do sanity check to see if tsIn, tsOut match tsProcIn, tsProcOut
			MonitorPerfAgg agg = window.get(monitor);
			agg.tWindowEnd = now;
			outputCollector.emit(new Values(monitor, agg));
		}
		currentWindowStart = now;
		window.clear();
	}

	private MonitorPerfAgg getMonitorAgg(String monitor) {
		MonitorPerfAgg agg = window.get(monitor);
		if (agg == null) {
			agg = new MonitorPerfAgg();
			agg.monitor = monitor;
			agg.tWindowStart = currentWindowStart;
			window.put(monitor, agg);
		}
		return agg;
	}
}

