package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPacket extends EventComponentClass {
	public static final Logger LOG = LoggerFactory.getLogger(ClassPacket.class);
	
	public ClassPacket() {
		super("PACKET", ProdUtils.MAX_PACKETTYPE, 
			ProdUtils.MAX_PACKETID, ProdUtils.MAX_PACKET_BREATH);
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

		PacketComponent comp = new PacketComponent(this, parent, type, id, strType, strID);
		
		return comp;
	}
	
}

