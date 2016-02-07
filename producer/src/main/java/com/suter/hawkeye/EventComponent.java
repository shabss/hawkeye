package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventComponent {

	public static final Logger LOG = LoggerFactory.getLogger(EventComponent.class);

	EventComponentClass compClass;
	EventComponent parent;
	Integer type;
	public Integer id;

	String strType;
	String strID;	
	int typeBreath;
	
	List<EventComponent> subComps;
	Random random;
		
	public EventComponent(EventComponentClass compClass, EventComponent parent, 
							Integer type, Integer id, String strType, String strID) {
		this.compClass = compClass;
		this.parent = parent;
		this.type = type;
		this.id = id;
		this.strType = strType;
		this.strID = strID;
		
		random = new Random();
		subComps = new ArrayList<EventComponent> ();
		typeBreath = random.nextInt(compClass.maxBreathPerType) + 1;
	}

	public void init() {
		if (subComps != null) {
			for (EventComponent ec : subComps) {
				ec.init();
			}
		}
	}
	
	public void fillCascade(HawkeyeEvent event) {
		//System.out.println("fillCascade:" + strID);
		fill(event);
		if (parent != null) {
			parent.fillCascade(event);
		}
	}
	
	public void fanOut() {
		//System.out.println("fanOut:" + strID);
		for (EventComponent ec : subComps) {
			ec.fanOut();
		}
	}
	
	public void fill(HawkeyeEvent event) {

		HawkeyeMonitor monID = new HawkeyeMonitor(ProdUtils.MON_TYPE_ID, 
			compClass.prefix + "ID", strID, ++event.totalPower);
		HawkeyeMonitor monType = new HawkeyeMonitor(ProdUtils.MON_TYPE_TYPE, 
			compClass.prefix + "TYPE", strType, ++event.totalPower);
		
		event.monitorGroup.add(monID);
		event.monitorGroup.add(monType);
	}
	
	abstract public void createSubComponents();
}



