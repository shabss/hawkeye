
package com.suter.hawkeye;
import java.util.Random;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
 
public class HawkeyeKafkaPartitioner implements Partitioner {
	Random random;
	public HawkeyeKafkaPartitioner (VerifiableProperties props) {
		random = new Random();
	}
 
    public int partition(Object key, int nParts) {
		int part = random.nextInt(nParts);
		//String sKey = (String) key;
		//part = Integer.parseInt(sKey) % nParts;
		return part;
	}
}