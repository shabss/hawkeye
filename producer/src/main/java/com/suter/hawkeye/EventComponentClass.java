package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventComponentClass {
	public static final Logger LOG = LoggerFactory.getLogger(EventComponentClass.class);

	public String prefix;
	public int maxTypes;
	public int maxIDs;
	public int maxBreathPerType;
	Random random;
	
	public Map<Integer, String> typeNames;
	public Map<Integer, String> idNames;
	public Map<Integer, List<Integer> > typeIDRelation;

	
	public EventComponentClass(String prefix, int maxTypes, 
			int maxIDs, int maxBreathPerType) {
		this.prefix = prefix;
		this.maxTypes = maxTypes;
		this.maxIDs = maxIDs;
		this.maxBreathPerType = maxBreathPerType;
		random = new Random();
	}

	public EventComponent createEventComponent(EventComponent parent) {
		String strType = null;
		String strID = null;
		int type = 0;
		int id = 0;
		List <Integer> relation = null;
		
		type = random.nextInt(maxTypes);
		if (typeNames != null) {
			strType = typeNames.get(type);			
		}
		if (strType == null) {
			strType = prefix + "TYPE" + type;
		}
		
		//if strType was null should we try for relationship?
		//always do it for now
		relation = typeIDRelation != null ? typeIDRelation.get(type) : null;
		if (relation != null) {
			int size = relation.size();
			Integer idObj = relation.get(random.nextInt(size));
			if ((idObj != null) && (idNames != null)) {
				strID = idNames.get(idObj);
			}
		}
		
		if (strID == null) {
			//we get here when relation was null or
			//relation existed but did not have corresponding id
			id = random.nextInt(maxIDs);
			strID = prefix + "ID" + id;
		}
		return createEventComponent(parent, new Integer(type), 
			new Integer(id), strType, strID);
	}
	
	abstract public void init();
	abstract public EventComponent createEventComponent
		(EventComponent parent, Integer type, Integer id, 
		String strType, String strID);
}



