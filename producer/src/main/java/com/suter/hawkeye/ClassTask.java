package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassTask extends EventComponentClass {
	public static final Logger LOG = LoggerFactory.getLogger(ClassTask.class);

	public ClassTask() {
		super("TASK", ProdUtils.MAX_TASKTYPE, 
			ProdUtils.MAX_TASKID, ProdUtils.MAX_TASK_BREATH);
	}
	
	@Override
	public void init() {
		//create type names
		//create id names
		//create type_to_id_relations
	}
	

	@Override
	public EventComponent createEventComponent
		(EventComponent parent, Integer type, Integer id, String strType, String strID) {

		TaskComponent comp = new TaskComponent(this, parent, type, id, strType, strID);
		return comp;
	}
	
}

