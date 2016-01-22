#!/usr/bin/python
from pyspark.sql import SQLContext
from pyspark import SparkContext
from pyspark import SparkConf

#set following values in $SPARK_HOME/conf/spark-env.sh
#	SPARK_EXECUTOR_MEMORY=14G
#	SPARK_DRIVER_MEMORY=14G

master_ip = "ip-172-31-2-168"
master_public_dns = "ec2-52-34-46-84.us-west-2.compute.amazonaws.com"

conf = SparkConf().setAppName("hawkeye")
sc = SparkContext(conf=conf)
##sc = SparkContext("spark://" + master_ip + ":7077", "hawkeye")

from cassandra.cluster import Cluster
cluster = Cluster(['52.34.46.84', '52.89.61.14', '52.27.234.47', '52.24.233.165'])
session = cluster.connect('hawkeye3')

sqlsc = SQLContext(sc)
hemsgs = sqlsc.jsonFile("hdfs://" + master_public_dns + ":9000/camus/topics/hawkeye-prod1/*/*/*/*/*/*")
#hemsgs.count(); hemsgs.take(1)

# Row(
#		AppID=u'HawkEye', 
#		PacketID=u'PACKET38602', 
#		SwID=u'SWID19', 
#		SwType=u'SWTYPE63', 
#		TaskID=u'TASKID398', 
#		TaskType=u'TASKTYPE62', 
#		TsIn=642686, 
#		TsOut=700735
#	)
	
def aggTSDelta(heRDD, aggBy):
	agg = heRDD.map(lambda m: (m.__getattr__(aggBy), (m.TsOut - m.TsIn, 1))).reduceByKey(lambda a,b: (a[0] + b[0], a[1] + b[1]))
	return agg
	
def aggToCassandraPart(agg):
	from cassandra.cluster import Cluster
	if agg:
		cascluster = Cluster(['52.34.46.84', '52.89.61.14', '52.27.234.47', '52.24.233.165'])
		casSession = cascluster.connect('hawkeye3')
		for aggItem in agg:
			casSession.execute('INSERT INTO sliding_window_batch (monitor, ts_start, time_total, event_count) VALUES (%s, %s, %s, %s)', (aggItem[0], 0, aggItem[1][0], aggItem[1][1]))
		casSession.shutdown()
		cascluster.shutdown()
		

aggAppID	= aggTSDelta(hemsgs, "AppID")
aggSwID 	= aggTSDelta(hemsgs, "SwID")
aggSwType 	= aggTSDelta(hemsgs, "SwType")
aggTaskID	= aggTSDelta(hemsgs, "TaskID")
aggTaskType	= aggTSDelta(hemsgs, "TaskType")
#aggHwID 	= aggTSDelta(hemsgs, "HwID")
#aggHwType 	= aggTSDelta(hemsgs, "HwType")

#aggPacketID = aggTSDelta(hemsgs, "PacketID")
	 
rdd = (aggAppID.union(aggSwID)
		.union(aggSwType)
		.union(aggTaskID)
		.union(aggTaskType))
		#.union(aggHwID)
		#.union(aggHwType)
rdd.foreachPartition(aggToCassandraPart)

#sanity = aggAppID.count() + aggPacketID.count() + aggSwID.count() + aggSwType.count() + aggTaskID.count() + aggTaskType.count() - rdd.count()

