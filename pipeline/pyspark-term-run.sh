IPYTHON=1 $SPARK_HOME/bin/pyspark \
	--master spark://ip-172-31-2-168:7077 \
	--packages datastax:spark-cassandra-connector:1.5.0-RC1-s_2.11
	--executor-memory 14G
	--driver-memory 14G
