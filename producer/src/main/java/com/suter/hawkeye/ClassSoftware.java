package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassSoftware extends EventComponentClass {
	public static final Logger LOG = LoggerFactory.getLogger(ClassSoftware.class);

	public ClassSoftware() {
		super("SW", ProdUtils.MAX_SWTYPE, 
			ProdUtils.MAX_SWID, ProdUtils.MAX_SW_BREATH);
	}
	
	@Override
	public void init() {
		
		typeNames = new HashMap<Integer, String>();
		idNames = new HashMap<Integer, String>();
		typeIDRelation = new HashMap<Integer, List<Integer>>();
		
		Integer iOracle 		= new Integer(0);
		Integer iCassandra 		= new Integer(1);
		Integer iMysql 			= new Integer(2);
		Integer iKafka	 		= new Integer(3);
		Integer iStorm	 		= new Integer(4);
		Integer iSpark	 		= new Integer(5);
		Integer iHttpd	 		= new Integer(6);
		Integer iRedis	 		= new Integer(7);
		Integer iSparkStreaming	= new Integer(8);
		Integer iFlask	 		= new Integer(9);
		Integer iPostgres 		= new Integer(10);
		
		typeNames.put(iOracle, 		"oracle");
		typeNames.put(iCassandra, 	"cassandra");
		typeNames.put(iMysql, 		"mysql");
		typeNames.put(iKafka, 		"kafka");
		typeNames.put(iStorm, 		"storm");
		typeNames.put(iSpark, 		"spark");
		typeNames.put(iHttpd, 		"httpd");
		typeNames.put(iRedis, 		"redis");
		typeNames.put(iSparkStreaming, "spark-streaming");
		typeNames.put(iFlask, 		"flask");
		//typeNames.put(iPostgres, 	"postgres");
		
		//create type names
		//create id names
		//create type_to_id_relations
	}	
	
	@Override
	public EventComponent createEventComponent
		(EventComponent parent, Integer type, Integer id, String strType, String strID) {

		SoftwareComponent comp = new SoftwareComponent(this, parent, type, id, strType, strID);
		
		return comp;
	}
	
}

