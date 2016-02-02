
package com.suter.hawkeye;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
 
public class HawkeyeKafkaPartitioner implements Partitioner {
	public HawkeyeKafkaPartitioner (VerifiableProperties props) {
 
	}
 
    public int partition(Object key, int nParts) {
		int part = 0;
		String sKey = (String) key;
		part = Integer.parseInt(sKey) % nParts;
		return part;
	}
}