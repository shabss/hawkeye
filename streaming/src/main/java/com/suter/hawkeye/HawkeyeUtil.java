package com.suter.hawkeye;

class HawkeyeUtil {
	public static String zkIp = "52.34.46.84";
	public static String nimbusHost = "52.34.46.84";
	public static String zookeeperHost = zkIp +":2181";	
	public static String hawkeyeTopic = "hawkeye4";
	public static String hawkeyeKeySpace = "hawkeye4";
	public static int kafkaPartitions = 128;
	
	public static long getProcWindowTime() {
		return System.currentTimeMillis();
	}
	
	
	
	
}