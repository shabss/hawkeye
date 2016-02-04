package com.suter.hawkeye;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

class ProdUtils {

	public static int MAX_APPTYPE 			= 1;
	public static int MAX_APPID				= 1;
	public static int MAX_APP_BREATH		= 100;

	public static int MAX_SWTYPE 			= 25;
	public static int MAX_SWID				= 100;
	public static int MAX_SW_BREATH			= 100;
	
	public static int MAX_TASKTYPE 			= 1000;
	public static int MAX_TASKID			= 1000;
	public static int MAX_TASK_BREATH		= 1000;
	
	public static int MAX_HWTYPE			= 100;
	public static int MAX_HWID				= 100000;
	public static int MAX_DEVICETYPE		= 1000;
	public static int MAX_DEVICEID			= 1000;

	public static int MAX_PACKETTYPE 		= 1;
	public static int MAX_PACKETID			= 100000;
	public static int MAX_PACKET_BREATH		= 1;
	
	public static int MAX_PACKET_DELAY		= 100000;
	public static int MAX_MESSAGES			= 1000000;
	
	public static String MON_TYPE_ID = "I";
	public static String MON_TYPE_TYPE = "T";
	
	public static EventComponentClass swCompClass;
	public static EventComponentClass taskCompClass;
	public static EventComponentClass appCompClass;
	public static EventComponentClass packetCompClass;
	
	public static AppComponent app;
	
	//parse args
	public static Integer appID;
	public static String  appName;
	public static boolean printOnly = false;
	public static boolean printFull = false;
	//end parse args
	
	public static String hawkeyeTopic = "hawkeye4";
	public static String kafkaBrokers = 
		"54.148.25.241:9092," + 
		"54.148.24.60:9092," + 
		"54.69.151.76:9092," + 
		"52.89.156.217:9092," +
		"52.89.121.49:9092";
		
	public static Producer<String, String> kafkaProducer;
	
	public static long getEventTime() {
		return System.currentTimeMillis();
	}

	public static void createKafkaProducer() {
        Properties props = new Properties();
        props.put("metadata.broker.list", kafkaBrokers);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
		//props.put("key.serializer.class", "kafka.serializer.DefaultEncoder");
        props.put("partitioner.class", "com.suter.hawkeye.HawkeyeKafkaPartitioner");
        props.put("request.required.acks", "0");
 
        ProducerConfig config = new ProducerConfig(props); 
        kafkaProducer = new Producer<String, String>(config);
	}
}





