package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskComponent extends EventComponent {

	public static final Logger LOG = LoggerFactory.getLogger(TaskComponent.class);
	
	public TaskComponent
		(EventComponentClass compClass, EventComponent parent, 
		Integer type, Integer id, String strType, String strID) {
		super(compClass, parent, type, id, strType, strID);
	}
	
	@Override
	public void createSubComponents() {
		for (int i = 0; i < typeBreath; i++) {
			EventComponent comp = ProdUtils.packetCompClass.createEventComponent(this);
			subComps.add(comp);
			comp.createSubComponents();			
		}
	}

	public void fill(HawkeyeEvent event) {
		event.TaskType = strType;
		event.TaskID = strID;
	}	
}


