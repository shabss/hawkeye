package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppComponent extends EventComponent {

	public static final Logger LOG = LoggerFactory.getLogger(AppComponent.class);

	public AppComponent
		(EventComponentClass compClass, EventComponent parent, 
		Integer type, Integer id, String strType, String strID) {
		super(compClass, parent, type, id, strType, strID);
	}
	
	@Override
	public void init() {
		ProdUtils.createKafkaProducer();
		super.init();
	}
	
	@Override
	public void createSubComponents() {
		for (int i = 0; i < typeBreath; i++) {
			EventComponent comp = ProdUtils.swCompClass.createEventComponent(this);
			subComps.add(comp);
			comp.createSubComponents();
		}
	}
	
	@Override
	public void fill(HawkeyeEvent event) {
		event.AppID = strID;
	}
	
	public void startEmit() {
		while (true) {
			fanOut();
		}
	}
	
}

