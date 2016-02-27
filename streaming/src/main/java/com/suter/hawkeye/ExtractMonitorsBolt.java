package com.suter.hawkeye;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//public class ExtractMonitorsBolt extends BaseBasicBolt {
public class ExtractMonitorsBolt extends BaseRichBolt {
	public static final Logger LOG = LoggerFactory.getLogger(ExtractMonitorsBolt.class);
	private OutputCollector richOutputCollector;
	
	@Override
	public void prepare(Map config, TopologyContext context, OutputCollector collector) {
		richOutputCollector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("monitor", "tsIn", "tsOut", "tDelta"));
	}

	@Override
	//public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
	public void execute(Tuple tuple) {
		String strEvent = tuple.getStringByField("event");
		Gson gson = new Gson();
		HawkeyeEvent event = gson.fromJson(strEvent, HawkeyeEvent.class);
		
		if (event != null) {
			for (HawkeyeMonitor mon: event.monitorGroup) {
				//emitMonitor(outputCollector, event, mon);
				emitMonitor(tuple, event, mon);
			}
		} else {
			
		}
		richOutputCollector.ack(tuple);
	}
	
	//public void emitMonitor(BasicOutputCollector outputCollector, 
	//						HawkeyeEvent event, HawkeyeMonitor mon) {
	public void emitMonitor(Tuple tuple, HawkeyeEvent event, HawkeyeMonitor mon) {

		long tDelta = event.tsOut - event.tsIn;
		//To Do:
		//	enrich emit: include mon.type, mon.subgroup also
		//	load balance by looking at mon.power
		try {
			richOutputCollector.emit(tuple, 
				new Values(mon.id, event.tsIn, event.tsOut, tDelta));
		} catch (Exception e) {
			
		}
	}
}


