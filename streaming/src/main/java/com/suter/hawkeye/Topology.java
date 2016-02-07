package com.suter.hawkeye;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;

import storm.kafka.KafkaSpout;
import storm.kafka.ZkHosts;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;

import backtype.storm.tuple.Fields;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Topology {
	public static final Logger LOG = LoggerFactory.getLogger(Topology.class);

	public static void main ( String[] args ) {
		//consumerTestMain(args);
		hawkeyeMain(args);
	}
	
	public static void consumerTestMain ( String[] args ) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-spout", createKafkaSpout(), 
			HawkeyeUtil.kafkaPartitions);
		//builder.setBolt("send-alert", new SendAlertBolt())
		//	.shuffleGrouping("kafka-spout");

		Config config = new Config();
		//config.setDebug(true);
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("hawkeye-streaming", config, builder.createTopology());
		
		Utils.sleep(600000);
		localCluster.shutdown();
	}

    public static void hawkeyeMain ( String[] args )
    {
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("kafka-spout", createKafkaSpout());
		builder.setBolt("extract-monitors", new ExtractMonitorsBolt())
			.shuffleGrouping("kafka-spout");
		builder.setBolt("now-window", new NowWindowBolt())
			.fieldsGrouping("extract-monitors", new Fields("monitor"));
		builder.setBolt("db-persist", new DBPersistBolt())
			.shuffleGrouping("now-window");

		builder.setBolt("history-window", new HistoryWindowBolt())
			.fieldsGrouping("extract-monitors", new Fields("monitor"));
		builder.setBolt("history-persist", new PersistHistoryBolt())
			.shuffleGrouping("history-window");

		Config config = new Config();
		config.setDebug(true);
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("hawkeye-streaming", config, builder.createTopology());
		
		Utils.sleep(600000);
		localCluster.shutdown();
    }
	
	public static KafkaSpout createKafkaSpout() {
        ZkHosts zkHosts = new ZkHosts(HawkeyeUtil.zookeeperHost);
        SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, 
			HawkeyeUtil.hawkeyeTopic, "", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme() {
            @Override
            public Fields getOutputFields() {
                return new Fields("event");
            }
        });

        KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);
		return kafkaSpout;
	}
}

