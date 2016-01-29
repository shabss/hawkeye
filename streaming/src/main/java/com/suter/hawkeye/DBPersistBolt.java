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


public class DBPersistBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(DBPersistBolt.class);
	Session casSession;
	PreparedStatement monProcWindowStmt;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//declarer.declare(new Fields("monitor", "tTotal", "tMax", "tMin", "tsProc"));
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {
		LOG.info("DBPersistBolt.prepare: enter");
		Cluster cluster = Cluster.builder().addContactPoint(HawkeyeUtil.zkIp).build();
		casSession = cluster.connect(HawkeyeUtil.hawkeyeKeySpace);
		monProcWindowStmt = casSession.prepare(
			"INSERT INTO monitor_proc_window (" +
				"monitor, tsInMin, tsInMax, tsOutMin, tsOutMax," +
				"tDeltaAgg, nEvents, tProcIn, tProcOut) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
		LOG.info("DBPersistBolt.prepare: done");
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {

		//LOG.info("DBPersistBolt.execute: 1");
		//LOG.info("DBPersistBolt.execute: tuple is: " + tuple);
		String monitor  = tuple.getStringByField("monitor");
		//LOG.info("ProcWindowBolt.execute: 2");
		MonitorProcWindow mpw = (MonitorProcWindow) tuple.getValueByField("procWindow");
		//LOG.info("DBPersistBolt.execute: 3");
		
		BoundStatement boundStatement = new BoundStatement(monProcWindowStmt);
		//LOG.info("DBPersistBolt.execute:4: monitor=" + mpw.monitor);
		casSession.execute(boundStatement.bind(
			mpw.monitor, mpw.tsInMin, mpw.tsInMax, mpw.tsOutMin, mpw.tsOutMax, 
			mpw.tDeltaAgg, mpw.nEvents, mpw.tProcIn, mpw.tProcOut));
		//LOG.info("DBPersistBolt.execute:5: monitor=" + monitor);
	}
}

