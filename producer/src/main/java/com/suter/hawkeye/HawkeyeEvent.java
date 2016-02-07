
package com.suter.hawkeye;
import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

public class HawkeyeEvent implements Serializable {
	public long tsIn;
	public long tsOut;
	public String packetID;
	public int totalPower;
	public List <HawkeyeMonitor> monitorGroup;
	

	
	public HawkeyeEvent() {
		monitorGroup = new ArrayList<HawkeyeMonitor>();
		totalPower = 0;
	}
	
  
	public void shallowInit() {
		totalPower = 0;
		monitorGroup.clear();
	}

	@Override
	public String toString() {
		String strHE = "{" +
			"\"tsIn\":" 	+ tsIn 	+ ", " +
			"\"tsOut\":" 	+ tsOut + ", " +
			"\"packetID\":\"" + packetID + "\", " +
			"\"monitorGroup\": [";
		
		for (HawkeyeMonitor mon: monitorGroup) {
			strHE += mon.toString() + ",";
		}
		strHE = strHE.substring(0, strHE.length()-1) + "]}";
		return strHE;
	}
}



