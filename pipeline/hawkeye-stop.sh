#kill storm topology
ssh 2nnk "sudo $STORM_HOME/bin/storm kill hawkeye-streaming-10-1455305030 -w 1"


#kill storm nimbus
ssh 2nnk "sudo ps -aef | grep storm | grep nimbus | grep java | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2nnk "sudo kill -9 <output of above>"

#kill storm supervisor
ssh 2dn1k "sudo ps -aef | grep storm | grep supervisor | grep java | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn2k "sudo ps -aef | grep storm | grep supervisor | grep java | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn3k "sudo ps -aef | grep storm | grep supervisor | grep java | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn4k "sudo ps -aef | grep storm | grep supervisor | grep java | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"

ssh 2dn1k "sudo kill -9 <output of above>"
ssh 2dn2k "sudo kill -9 <output of above>"
ssh 2dn3k "sudo kill -9 <output of above>"
ssh 2dn4k "sudo kill -9 <output of above>"

#kill kafka
ssh 2nnk "sudo /usr/local/kafka/bin/kafka-server-stop.sh"
ssh 2dn1k "sudo /usr/local/kafka/bin/kafka-server-stop.sh"
ssh 2dn2k "sudo /usr/local/kafka/bin/kafka-server-stop.sh"
ssh 2dn3k "sudo /usr/local/kafka/bin/kafka-server-stop.sh"
ssh 2dn4k "sudo /usr/local/kafka/bin/kafka-server-stop.sh"

#kill zookeeper
ssh 2nnk "sudo /usr/local/zookeeper/bin/zkServer.sh stop"
ssh 2dn1k "sudo /usr/local/zookeeper/bin/zkServer.sh stop"
ssh 2dn2k "sudo /usr/local/zookeeper/bin/zkServer.sh stop"
ssh 2dn3k "sudo /usr/local/zookeeper/bin/zkServer.sh stop"
ssh 2dn4k "sudo /usr/local/zookeeper/bin/zkServer.sh stop"

#find cassandra pid
ssh 2nnh "sudo ps -aef | grep cassandra | grep javaagent | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn1h "sudo ps -aef | grep cassandra | grep javaagent | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn2h "sudo ps -aef | grep cassandra | grep javaagent | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn3h "sudo ps -aef | grep cassandra | grep javaagent | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"
ssh 2dn4h "sudo ps -aef | grep cassandra | grep javaagent | sed 's/\s\s*/ /g' | cut -d ' ' -f 2"

ssh 2nnh "sudo kill -9 "
ssh 2dn1h "sudo kill -9 "
ssh 2dn2h "sudo kill -9 "
ssh 2dn3h "sudo kill -9 "
ssh 2dn4h "sudo kill -9 "

ssh 2nnh "nodetool status"


