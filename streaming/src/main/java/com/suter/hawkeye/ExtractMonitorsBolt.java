package com.suter.hawkeye;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;

import com.google.gson.Gson;

public class ExtractMonitorsBolt extends BaseBasicBolt {

	
	@Override
	public void prepare(Map config, TopologyContext context) {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("monitor", "tsIn", "tsOut", "tDelta"));
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
		String strEvent = tuple.getStringByField("event");
		Gson gson = new Gson();
		HawkeyeEvent event = gson.fromJson(strEvent, HawkeyeEvent.class);
		
		for (HawkeyeMonitor mon: event.monitorGroup) {
			emitMonitor(outputCollector, event, mon);
		}
	}
	
	public void emitMonitor(BasicOutputCollector outputCollector, 
							HawkeyeEvent event, HawkeyeMonitor mon) {
		long tDelta = event.tsOut - event.tsIn;
		//To Do:
		//	enrich emit: include mon.type, mon.subgroup also
		//	load balance by looking at mon.power
		outputCollector.emit(new Values(mon.id, event.tsIn, event.tsOut, tDelta));
	}

	
}
