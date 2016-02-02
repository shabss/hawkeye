

package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassApp extends EventComponentClass {
	
	public static final Logger LOG = LoggerFactory.getLogger(ClassApp.class);
	
	public ClassApp() {
		super("APP", ProdUtils.MAX_APPTYPE, 
			ProdUtils.MAX_APPID, ProdUtils.MAX_APP_BREATH);
	}
	
	
	@Override
	public void init() {

	}

	@Override
	public EventComponent createEventComponent
		(EventComponent parent, Integer type, Integer id, String strType, String strID) {

		AppComponent comp = new AppComponent(this, parent, type, id, strType, strID);
		ProdUtils.app = comp;
		return comp;
	}
}

