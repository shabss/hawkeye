package com.suter.hawkeye;

class HawkeyeUtil {
	/* 
	public static String zkIp = "52.34.46.84";
	public static String nimbusHost = "52.34.46.84";
	public static String zookeeperHost = zkIp +":2181";	
	public static String hawkeyeTopic = "hawkeye4";
	public static String hawkeyeKeySpace = "hawkeye4";
	public static int kafkaPartitions = 128;
	*/

	public static String 	cassandraHost = "52.34.253.146";
	public static String 	zkIp = "54.148.25.241";
	public static String 	nimbusHost = "54.148.25.241";
	public static String 	zookeeperHost = zkIp +":2181";	
	public static String 	hawkeyeTopic = "hawkeye4";
	public static String 	hawkeyeKeySpace = "hawkeye4";
	public static int 		kafkaPartitions = 10;
	public static long 		historyWindowSizeMS = 600000; //10 mins
	public static long		nowWindowSizeMS = 1000;
	
	//jedis values
	public static String 	jedisHost = nimbusHost;
	public static int		jedisPort = 6379;
	public static int 		jedisTimeout = 3600; //1hour
	
	public static String 	nowJedisSuffix = "_now";
	public static String 	histJedisSuffix = "_hist";
	
	public static long getTime() {
		return System.currentTimeMillis();
	}
}

