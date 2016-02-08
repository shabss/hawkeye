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
import java.util.Calendar;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.BoundStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistHistoryBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(PersistHistoryBolt.class);
	private Session casSession;
	private PreparedStatement persistStmt;
	private Jedis jedis;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {
		LOG.info("PersistHistoryBolt.prepare: enter");
		Cluster cluster = Cluster.builder().addContactPoint(HawkeyeUtil.cassandraHost).build();
		casSession = cluster.connect(HawkeyeUtil.hawkeyeKeySpace);
		persistStmt = casSession.prepare(
			"INSERT INTO monitor_history (" +
				"monitor, record_time_year, record_time_ms, tDeltaAgg, nEvents, time_window_size_ms" +
			") VALUES (?, ?, ?, ?, ?, ?)");
		
		jedis = new Jedis(HawkeyeUtil.nimbusHost);
		jedis.ping();
		LOG.info("PersistHistoryBolt.prepare: done");
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
		String monitor  = tuple.getStringByField("monitor");
		MonitorPerfAgg agg = (MonitorPerfAgg) tuple.getValueByField("agg");
		
		jedis.rpush(monitor + HawkeyeUtil.histJedisSuffix, 
			new Double((double)agg.tDeltaAgg/agg.nEvents).toString());

		long now = HawkeyeUtil.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		long year = c.get(Calendar.YEAR);
		
		BoundStatement boundStatement = new BoundStatement(persistStmt);
		casSession.execute(boundStatement.bind(
			agg.monitor, year, new Date(now), agg.tDeltaAgg, 
			agg.nEvents, HawkeyeUtil.historyWindowSizeMS));
			
			
		
	}
}

