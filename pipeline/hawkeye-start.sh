#start hadoop/hdfs, yarn, historyserver
ssh 2nnh "/usr/local/hadoop/sbin/start-dfs.sh"
ssh 2nnh "/usr/local/hadoop/sbin/start-yarn.sh"
ssh 2nnh "/usr/local/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver"
ssh 2nnh "jps"

#start cassandra
ssh 2nnh "/usr/local/cassandra/bin/cassandra"
ssh 2dn1h "/usr/local/cassandra/bin/cassandra"
ssh 2dn2h "/usr/local/cassandra/bin/cassandra"
ssh 2dn3h "/usr/local/cassandra/bin/cassandra"
ssh 2dn4h "/usr/local/cassandra/bin/cassandra"
ssh 2nnh "nodetool status"

#start spark
ssh 2nnh "/usr/local/spark/sbin/start-all.sh"



#start hadoop/hdfs, yarn, historyserver
#ssh 2nnh "/usr/local/hadoop/sbin/start-dfs.sh"
#ssh 2nnh "/usr/local/hadoop/sbin/start-yarn.sh"
#ssh 2nnh "/usr/local/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver"
#ssh 2nnh "jps"
#start zookeeper
ssh 2nnk "sudo /usr/local/zookeeper/bin/zkServer.sh start"
ssh 2dn1k "sudo /usr/local/zookeeper/bin/zkServer.sh start"
ssh 2dn2k "sudo /usr/local/zookeeper/bin/zkServer.sh start"
ssh 2dn3k "sudo /usr/local/zookeeper/bin/zkServer.sh start"
ssh 2dn4k "sudo /usr/local/zookeeper/bin/zkServer.sh start"

ssh 2nnk "echo srvr | nc localhost 2181 | grep Mode"
ssh 2dn1k "echo srvr | nc localhost 2181 | grep Mode";
ssh 2dn2k "echo srvr | nc localhost 2181 | grep Mode";
ssh 2dn3k "echo srvr | nc localhost 2181 | grep Mode";
ssh 2dn4k "echo srvr | nc localhost 2181 | grep Mode";


#start kafka
ssh 2nnk "sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties &"
ssh 2dn1k "sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties &"
ssh 2dn2k "sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties &"
ssh 2dn3k "sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties &"
ssh 2dn4k "sudo /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties &"


#start storm
ssh 2nnk "sudo /usr/local/storm/bin/storm nimbus &"
ssh 2dn1k "sudo /usr/local/storm/bin/storm supervisor &"
ssh 2dn2k "sudo /usr/local/storm/bin/storm supervisor &"
ssh 2dn3k "sudo /usr/local/storm/bin/storm supervisor &"
ssh 2dn4k "sudo /usr/local/storm/bin/storm supervisor &"
ssh 2nnk "sudo /usr/local/storm/bin/storm ui &"


#start redis
ssh 2nnk "sudo /usr/local/redis/src/redis-server /usr/local/redis/redis.conf &"
