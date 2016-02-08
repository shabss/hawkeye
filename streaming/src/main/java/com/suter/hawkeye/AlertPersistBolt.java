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


public class AlertPersistBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(AlertPersistBolt.class);
	Session casSession;
	PreparedStatement monProcWindowStmt;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {
		LOG.info("AlertPersist.prepare: enter");
		Cluster cluster = Cluster.builder().addContactPoint(HawkeyeUtil.cassandraHost).build();
		casSession = cluster.connect(HawkeyeUtil.hawkeyeKeySpace);
		monProcWindowStmt = casSession.prepare(
			"INSERT INTO monitor_alerts (" +
				"monitor, alert_time_year, alert_time_ms, alert_through, alert_sev," +
				"min_through, sigma2neg_through, sigma1neg_through, sigma1pos_through," +
				"sigma2pos_through, max_through) VALUES " +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
		LOG.info("AlertPersist.prepare: done");
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {

		//LOG.info("DBPersistBolt.execute: 1");
		//LOG.info("DBPersistBolt.execute: tuple is: " + tuple);
		long now = HawkeyeUtil.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		long year = c.get(Calendar.YEAR);
		
		String monitor  = tuple.getStringByField("monitor");
		String sev = tuple.getStringByField("sev");
		MonitorPerfAgg agg = (MonitorPerfAgg) tuple.getValueByField("agg");
		//LOG.info("DBPersistBolt.execute: 3");
		
		BoundStatement boundStatement = new BoundStatement(monProcWindowStmt);
		//LOG.info("DBPersistBolt.execute:4: monitor=" + mpw.monitor);
		casSession.execute(boundStatement.bind(
			agg.monitor, year, new Date(now), (double)agg.tDeltaAgg/agg.nEvents, sev, 
			agg.min, agg.sig2neg, agg.sig1neg, agg.sig1pos, agg.sig2pos, agg.max));
		//LOG.info("DBPersistBolt.execute:5: monitor=" + monitor);
	}
}

