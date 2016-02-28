
# Hawkeye Streaming App
<to do>

## Configuration
<to do>

### External Dependencies
Download and copy the following jars into $STORM_HOME/libs

* cassandra-driver-core-2.1.9.jar
* kafka_2.10-0.8.2.1.jar
* commons-math3-3.5.jar
* kafka-clients-0.8.2.1.jar
* curator-client-2.5.0.jar
* metrics-core-2.2.0.jar
* curator-client-3.0.0.jar
* metrics-core-3.1.2.jar
* curator-framework-2.5.0.jar
* netty-3.10.5.Final.jar
* curator-framework-3.0.0.jar
* netty-all-4.0.32.Final.jar
* gson-2.5.jar
* scala-library-2.10.4.jar
* guava-18.0.jar
* storm-kafka-0.10.0.jar
* jedis-2.7.2.jar
* zookeeper-3.4.6.jar
* json-simple-1.1.1.jar


## Build
* Checkout : git clone ...
* cd ./hawkeye/streaming
* mvn clean package

## Execute
* Storm, kafka, zookeeper, redis should be running (see Configuration section)
* storm jar target/streaming-??.jar com.suter.hawkeye.Toplogy
