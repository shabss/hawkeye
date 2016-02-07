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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistHistoryBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(PersistHistoryBolt.class);
	Session casSession;
	PreparedStatement persistStmt;
	
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
				
		LOG.info("PersistHistoryBolt.prepare: done");
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
		long year = 0;
		String monitor  = tuple.getStringByField("monitor");
		MonitorPerfAgg agg = (MonitorPerfAgg) tuple.getValueByField("agg");
		BoundStatement boundStatement = new BoundStatement(persistStmt);
		casSession.execute(boundStatement.bind(
			mpw.monitor, year, HawkeyeUtil.getTime(), mpw.tDeltaAgg, 
			mpw.nEvents, HawkeyeUtil.historyWindowSize));
	}
}

