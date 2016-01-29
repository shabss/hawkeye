package com.suter.hawkeye;

import java.io.Serializable;

public class MonitorProcWindow implements Serializable {
	public String monitor;
	public long tsInMin;
	public long tsInMax;
	public long tsOutMin;
	public long tsOutMax;
	public long tDeltaAgg;
	public long nEvents;
	public long tProcIn;
	public long tProcOut;
	
	
	public MonitorProcWindow() {
		monitor = "";
		tsInMin = Long.MAX_VALUE;
		tsInMax = 0;
		tsOutMin = Long.MAX_VALUE;;
		tsOutMax = 0;
		tDeltaAgg = 0;
		nEvents = 0;
		tProcIn = 0;
		tProcOut = 0;
		
		
	}
	@Override
	public String toString() {
		return "MonitorProcWindow {" +
			"monitor=\'" + 	monitor + '\'' +
			", tsInMin=" + 	tsInMin +
			", tsInMax=" + 	tsInMax + 
			", tsOutMin=" + tsOutMin + 
			", tsOutMax=" + tsOutMax + 
			", tDeltaAgg=" + tDeltaAgg +
			", nEvents=" + 	nEvents +
			", tProcIn=" + tProcIn +
			", tProcOut=" + tProcOut +
		'}';
	}
}