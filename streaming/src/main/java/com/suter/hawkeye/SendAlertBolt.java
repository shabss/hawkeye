package com.suter.hawkeye;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

public class SendAlertBolt extends BaseBasicBolt {


  @Override
  public void prepare(Map config,
                      TopologyContext context) {

  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
    // nothing to declare
  }

  @Override
  public void execute(Tuple tuple,
                      BasicOutputCollector outputCollector) {
						  
  }
}
