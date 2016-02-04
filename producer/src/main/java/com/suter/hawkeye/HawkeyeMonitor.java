package com.suter.hawkeye;
 
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HawkeyeMonitor implements Serializable {
	public static final Logger LOG = LoggerFactory.getLogger(HawkeyeMonitor.class);

	public String type;
	public String subgroup;
	public String id;
	public int power;
	
	public HawkeyeMonitor(String type, String subgroup, String id, int power) {
		this.type = type;
		this.subgroup = subgroup;
		this.id = id;
		this.power = power;
	}
	
	public String toString() {
		return 
		"{" + 
			"\"type\" : \"" + type + "\", " +
			"\"subgroup\" : \"" + subgroup + "\", " +
			"\"id\" : \"" 	+ id + "\", " +
			"\"power\" : \"" + power + "\"" +
		"}";
	}
}