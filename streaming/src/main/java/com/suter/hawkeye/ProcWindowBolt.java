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


public class ProcWindowBolt extends BaseBasicBolt {
	
	public static final Logger LOG = LoggerFactory.getLogger(ProcWindowBolt.class);
	private Map<String, MonitorProcWindow> procWindow;
	private long currentProcWindowStart;
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("monitor", "procWindow"));
	}

	@Override
	public void prepare(Map stormConf,
						TopologyContext context) {
		procWindow = new HashMap<String, MonitorProcWindow>();
		currentProcWindowStart = HawkeyeUtil.getProcWindowTime();
		LOG.info("ProcWindowBolt.prepare: done");
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 1);
		return conf;
	}
	
	@Override
	public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
		//LOG.info("ProcWindowBolt.execute: 1");
		if (isTickTuple(tuple)) {
			//LOG.info("ProcWindowBolt.execute: 2");
			emitProcWindowAggregates(outputCollector);
			//LOG.info("ProcWindowBolt.execute: 3");
		} else {
			//LOG.info("ProcWindowBolt.execute: 4");
			//LOG.info("ProcWindowBolt.execute: tuple is: " + tuple);
			String monitor  = tuple.getStringByField("monitor");
			Long tsIn = tuple.getLongByField("tsIn");
			Long tsOut = tuple.getLongByField("tsOut");
			Long tDelta = tuple.getLongByField("tDelta");
			//LOG.info("ProcWindowBolt.execute: 5");
			
			MonitorProcWindow monitorProcWindow = getMonitorProcWindow(monitor);
			monitorProcWindow.nEvents++;
			monitorProcWindow.tDeltaAgg += tDelta;
			monitorProcWindow.tsInMin = Math.min(tsIn, monitorProcWindow.tsInMin);
			monitorProcWindow.tsInMax = Math.max(tsIn, monitorProcWindow.tsInMax);
			monitorProcWindow.tsOutMin = Math.min(tsOut, monitorProcWindow.tsOutMin);
			monitorProcWindow.tsOutMax = Math.max(tsOut, monitorProcWindow.tsOutMin);
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
			MonitorProcWindow mpw = procWindow.get(monitor);
			mpw.tProcOut = now;
			//LOG.info("ProcWindowBolt.persistProcWindowAggregates:4: monitor=" + monitor);
			casSession.execute(boundStatement.bind(
				mpw.monitor, mpw.tsInMin, mpw.tsInMax, mpw.tsOutMin, mpw.tsOutMax, 
				mpw.tDeltaAgg, mpw.nEvents, mpw.tProcIn, mpw.tProcOut));
			//LOG.info("ProcWindowBolt.persistProcWindowAggregates:5: monitor=" + monitor);
		}
		currentProcWindowStart = now;
		procWindow.clear();
	}
*/
	private void emitProcWindowAggregates(BasicOutputCollector outputCollector) {
		Long now = HawkeyeUtil.getProcWindowTime();
		Set<String> monitorsAvailable = procWindow.keySet();
		for (String monitor : monitorsAvailable) {
			//to do, do sanity check to see if tsIn, tsOut match tsProcIn, tsProcOut
			MonitorProcWindow mpw = procWindow.get(monitor);
			mpw.tProcOut = now;
			outputCollector.emit(new Values(monitor, mpw));
		}
		currentProcWindowStart = now;
		procWindow.clear();
	}

	private MonitorProcWindow getMonitorProcWindow(String monitor) {
		MonitorProcWindow monitorProcWindow = procWindow.get(monitor);
		if (monitorProcWindow == null) {
			monitorProcWindow = new MonitorProcWindow();
			monitorProcWindow.monitor = monitor;
			monitorProcWindow.tProcIn = currentProcWindowStart;
			procWindow.put(monitor, monitorProcWindow);
		}
		return monitorProcWindow;
	}
}

