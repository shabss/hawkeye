package com.suter.hawkeye;

import java.io.Serializable;

public class  MonitorPerfAgg implements Serializable {
	public String monitor;
	
	
	//Below are for sanity checks
	public long tsInMin;
	public long tsInMax;
	public long tsOutMin;
	public long tsOutMax;
	
	
	//Below values are for maintaining aggregates
	public long tDeltaAgg;
	public long nEvents;
	public long tWindowStart;
	public long tWindowEnd;
	public long tWindowSize;
	
	//summary values
	public double min;
	public double sig2neg;
	public double sig1neg;
	public double sig1pos;
	public double sig2pos;
	public double max;
	
	public MonitorPerfAgg() {
		monitor = "";
		/*
		tsInMin = Long.MAX_VALUE;
		tsInMax = 0;
		tsOutMin = Long.MAX_VALUE;;
		tsOutMax = 0;
		*/
		tDeltaAgg = 0;
		nEvents = 0;
		tWindowStart = 0;
		tWindowEnd = 0;
		tWindowSize = 0;
		
		min = Double.NaN;
		sig2neg = Double.NaN;
		sig1neg = Double.NaN;
		sig1pos = Double.NaN;
		sig2pos = Double.NaN;
		max = Double.NaN;
	}
	
	@Override
	public String toString() {
		return "MonitorPerfAgg {" +
			"monitor=\'" 	+ monitor + '\'' +
			
			", tsInMin=" 	+ tsInMin +
			", tsInMax=" 	+ tsInMax + 
			", tsOutMin=" 	+ tsOutMin + 
			", tsOutMax=" 	+ tsOutMax + 
			
			", tDeltaAgg=" 	+ tDeltaAgg +
			", nEvents=" 	+ nEvents +
			", tWindowStart=" 	+ tWindowStart +
			", tWindowEnd=" 	+ tWindowEnd +
			", tWindowSize=" 	+ tWindowSize +
			", min=" 		+ min +
			", sig2neg=" 	+ sig2neg +
			", sig1neg=" 	+ sig1neg +
			", sig1pos=" 	+ sig1pos +
			", sig2pos=" 	+ sig2pos +
			", max=" 		+ max +
		'}';
	}
}