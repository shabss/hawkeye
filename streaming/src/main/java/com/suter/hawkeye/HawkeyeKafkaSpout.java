package com.suter.hawkeye;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import storm.kafka.ZkHosts;
import storm.kafka.KafkaSpout;

public class HawkeyeKafkaSpout extends BaseRichSpout {
	private SpoutOutputCollector outputCollector;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("timestamp"));
	}
	
	@Override
	public void open(Map map,
					TopologyContext topologyContext,
					SpoutOutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		/*
		BrokerHosts hosts = new ZkHosts(zkConnString);
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		*/
	}
	
	@Override
	public void nextTuple() {
		long time = System.nanoTime(); 
		outputCollector.emit(new Values(time), new Long(time));
	}
	
	@Override
	public void ack(Object msgId) {
		super.ack(msgId);
	}
	
	@Override
	public void fail(Object msgId) {
		super.fail(msgId);
	}
}

