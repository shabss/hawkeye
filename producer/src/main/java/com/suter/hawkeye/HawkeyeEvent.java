
package com.suter.hawkeye;
import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

public class HawkeyeEvent implements Serializable {
	public long TsIn;
	public long TsOut;
	public String PacketID;
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
			"\"tsIn\":" 	+ TsIn 	+ ", " +
			"\"tsOut\":" 	+ TsOut + ", " +
			"\"packetID\":\"" + PacketID + "\", " +
			"\"monitorGroup\": [";
		
		for (HawkeyeMonitor mon: monitorGroup) {
			strHE += mon.toString() + ",";
		}
		strHE = strHE.substring(0, strHE.length()-1) + "]}";
		return strHE;
	}
}



