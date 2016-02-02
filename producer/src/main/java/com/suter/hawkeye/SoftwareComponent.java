package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoftwareComponent extends EventComponent {
	
	public static final Logger LOG = LoggerFactory.getLogger(SoftwareComponent.class);
	public SoftwareComponent
		(EventComponentClass compClass, EventComponent parent,
		Integer type, Integer id, String strType, String strID) {
		super(compClass, parent, type, id, strType, strID);
	}
	
	@Override
	public void createSubComponents() {
		for (int i = 0; i < typeBreath; i++) {
			EventComponent comp = ProdUtils.taskCompClass.createEventComponent(this);
			subComps.add(comp);
			comp.createSubComponents();
		}
		//System.out.println("SoftwareComponent.createSubComponents: subComps.size = " + subComps.size());
	}
	
	@Override
	public void fill(HawkeyeEvent event) {
		event.SwType = strType;
		event.SwID = strID;
	}
}

