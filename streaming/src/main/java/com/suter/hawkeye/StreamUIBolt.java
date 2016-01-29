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


public class StreamUIBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(StreamUIBolt.class);
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//declarer.declare(new Fields("monitor", "tTotal", "tMax", "tMin", "tsProc"));
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {

	}
	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {

	}
}
