#!/usr/bin/python
from pyspark.sql import SQLContext
from pyspark import SparkContext
from pyspark import SparkConf

#set following values in $SPARK_HOME/conf/spark-env.sh
#	SPARK_EXECUTOR_MEMORY=14G
#	SPARK_DRIVER_MEMORY=14G

USE_OLD_CLUSTER = 0
CASSANDRA_KEYSPACE = 'hawkeye4'
KAFKA_TOPIC = 'hawkeye4'

if USE_OLD_CLUSTER == 1:
	master_ip = "ip-172-31-2-168"
	master_public_dns = "ec2-52-34-46-84.us-west-2.compute.amazonaws.com"
	worker_public_dns = ['52.34.46.84', '52.89.61.14', '52.27.234.47', '52.24.233.165'] 
else :
	master_ip = "ip-172-31-2-180"
	master_public_dns = "'ec2-52-34-253-146.us-west-2.compute.amazonaws.com"
	worker_public_dns = ['52.34.253.146', '52.27.28.14', '52.35.88.14', '52.32.240.173', '52.88.31.138']
conf = SparkConf().setAppName("hawkeye")
sc = SparkContext(conf=conf)
##sc = SparkContext("spark://" + master_ip + ":7077", "hawkeye")

from cassandra.cluster import Cluster
if USE_OLD_CLUSTER == 1:
	cluster = Cluster(worker_public_dns)
else:
	cluster = Cluster(worker_public_dns)
	
session = cluster.connect(CASSANDRA_KEYSPACE)

sqlsc = SQLContext(sc)
hemsgs = sqlsc.jsonFile("hdfs://" + master_public_dns + ":9000/camus/topics/" + KAFKA_TOPIC + "/*/*/*/*/*/*")
#hemsgs.count(); hemsgs.take(1)

# Row({
# 	"tsIn": 1454556923889,
# 	"tsOut": 1454556983787,
# 	"packetID": "PACKET19083",
# 	"monitorGroup": [
# 		{"type": "I",	"subgroup": "TASKID",	"id": "TASKID492", 	"power": "1"},
# 		{"type": "T",	"subgroup": "TASKTYPE",	"id": "TASKTYPE69",	"power": "2"},
# 		{"type": "I",	"subgroup": "SWID",		"id": "SWID6",		"power": "3"},
# 		{"type": "T",	"subgroup": "SWTYPE",	"id": "mysql",		"power": "4"},
# 		{"type": "I",	"subgroup": "APPID",	"id": "hawkeye",	"power": "5"},
# 		{"type": "T",	"subgroup": "APPTYPE",	"id": "APP",		"power": "6"}
# 	]
# })
	
def aggTSDelta(heRDD, aggBy):
	agg = heRDD.map(lambda m: (m.__getattr__(aggBy), (m.TsOut - m.TsIn, 1))).reduceByKey(lambda a,b: (a[0] + b[0], a[1] + b[1]))
	return agg
	
def aggToCassandraPart(agg):
	from cassandra.cluster import Cluster
	if agg:
		cascluster = Cluster(worker_public_dns)
		casSession = cascluster.connect(CASSANDRA_KEYSPACE)
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

