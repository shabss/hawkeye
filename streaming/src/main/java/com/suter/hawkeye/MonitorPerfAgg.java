package com.suter.hawkeye;

import java.io.Serializable;

public class  MonitorPerfAgg implements Serializable {
	public String monitor;
	
	//Below are for sanity checks
	public long tsInMin;
	public long tsInMax;
	public long tsOutMin;
	public long tsOutMax;
	public long tDeltaAgg;
	public long nEvents;
	public long tWindowStart;
	public long tWindowEnd;
	public long tWindowSize;
	
	public MonitorProcWindow() {
		monitor = "";
		tsInMin = Long.MAX_VALUE;
		tsInMax = 0;
		tsOutMin = Long.MAX_VALUE;;
		tsOutMax = 0;
		tDeltaAgg = 0;
		nEvents = 0;
		tWindowStart = 0;
		tWindowStart = 0;
		tWindowSize = 0;
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
			", tWindowStart=" + tWindowStart +
			", tWindowEnd=" + tWindowEnd +
			", tWindowSize=" tWindowSize +
		'}';
	}
}