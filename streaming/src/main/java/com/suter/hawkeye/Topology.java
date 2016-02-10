package com.suter.hawkeye;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.generated.StormTopology;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;

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
		//hawkeyeMainLocal(args);
		hawkeyeMainRemote(args);
	}
	
	public static void consumerTestMain ( String[] args ) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-spout", createKafkaSpout(), 
			HawkeyeUtil.kafkaPartitions);

		Config config = new Config();
		//config.setDebug(true);
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("hawkeye-streaming", config, builder.createTopology());
		
		Utils.sleep(600000);
		localCluster.shutdown();
	}

    public static void hawkeyeMainLocal ( String[] args )
    {
		StormTopology topo = buildHawkeyeTopology();
		
		Config config = new Config();
		config.setDebug(true);
		
		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("hawkeye-streaming", config, topo);
		
		Utils.sleep(600000);
		localCluster.shutdown();
    }

    public static void hawkeyeMainRemote ( String[] args )
    {
		try {
			StormTopology topo = buildHawkeyeTopology();
			Config config = new Config();
			//config.setDebug(true);
			config.setNumWorkers(10);
			
			StormSubmitter.submitTopology("hawkeye-streaming", config, topo);
		} catch (Exception ex) {
			throw new RuntimeException("Exception at hawkeyeMainRemote:", ex);
		} 
    }
	
	public static StormTopology buildHawkeyeTopology() {
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("kafka-spout", createKafkaSpout(), 20);
		builder.setBolt("extract-monitors", new ExtractMonitorsBolt(), 20)
			.shuffleGrouping("kafka-spout");
			
		builder.setBolt("now-window", new NowWindowBolt(), 10)
			.fieldsGrouping("extract-monitors", new Fields("monitor"));
		builder.setBolt("alert-persist", new AlertPersistBolt(), 10)
			.shuffleGrouping("now-window");

		builder.setBolt("history-window", new HistoryWindowBolt(), 10)
			.fieldsGrouping("extract-monitors", new Fields("monitor"));
		builder.setBolt("history-persist", new PersistHistoryBolt(), 5)
			.shuffleGrouping("history-window");
		return builder.createTopology();
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

