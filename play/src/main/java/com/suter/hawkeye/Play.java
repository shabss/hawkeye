package com.suter.hawkeye;

import redis.clients.jedis.Jedis;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import java.util.*;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Play {

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
	//public static String 	jedisHost = "localhost";
	public static String 	jedisHost = nimbusHost;
	public static int		jedisPort = 6379;
	public static int 		jedisTimeout = 1000; //1sec
	
	public static String 	nowJedisSuffix = "_now";
	public static String 	histJedisSuffix = "_hist";
	public static String 	testJestSuffix = "_test";
	
	public static void main(String args[]) {
		//testRedisMain(args);
		testKafkaMain(args)
	}
	
	public static void testKafkaMain(String args) {
		
		
	}
	public static void testRedisMain(String args[]){
		//Connecting to Redis server on localhost
		Jedis jedis = new Jedis(jedisHost, jedisPort, jedisTimeout);
		System.out.println("Connection to jedis server sucessfully");
		System.out.println("Server is running: "+jedis.ping());
		
		System.out.println("PersistHistoryBolt.prepare: enter");
		Cluster cluster = Cluster.builder().addContactPoint(cassandraHost).build();
		Session casSession = cluster.connect(hawkeyeKeySpace);
		
		String stmt = "SELECT monitor, tdeltaagg, nevents, time_window_size_ms " + 
			"FROM monitor_history WHERE monitor in " + 
			"('hawkeye') and record_time_year = 2016";
		System.out.println(stmt);
		
		ResultSet results = casSession.execute(stmt);
		for (Row row : results) {
			//store data in redis list
			String monitor 	= row.getString("monitor");
			Long tDeltaAgg 	= row.getLong("tdeltaagg");
			Long nEvents 	= row.getLong("nevents");
			Double through 	= (double) tDeltaAgg / nEvents;
			System.out.println("monitor:" + monitor + ", through="+through);
			jedis.lpush(monitor + testJestSuffix, through.toString());
		}

		

		List<String> list = jedis.lrange("hawkeye" + testJestSuffix, 0 ,-1);
		for(int i=0; i<list.size(); i++) {
			System.out.println("Stored value redis:: "+list.get(i));
		}
		
		SummaryStatistics history = new SummaryStatistics();
		for(int i=0; i<list.size(); i++) {
			history.addValue(Double.parseDouble(list.get(i)));
		}
		
		System.out.println("min=" + history.getMin());
		System.out.println("sd=" + history.getStandardDeviation());
		System.out.println("mean=" + history.getStandardDeviation());
		System.out.println("Min=" + history.getMean());
		System.out.println("max=" + history.getMax());
		
		
		
		System.out.println("PersistHistoryBolt.prepare: done");
		casSession.close();
		cluster.close();
	}
}

