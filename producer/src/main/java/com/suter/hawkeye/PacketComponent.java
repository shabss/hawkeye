package com.suter.hawkeye;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.google.gson.Gson;

public class PacketComponent extends EventComponent {
	
	HawkeyeEvent event;
	Gson gson;
	
	public PacketComponent
		(EventComponentClass compClass, EventComponent parent,
		Integer type, Integer id, String strType, String strID) {
		super(compClass, parent, type, id, strType, strID);
		subComps = null; //dont have subComps
		event = new HawkeyeEvent();
		gson = new Gson();
	}

	
	@Override
	public void fanOut() {
		fillCascade(event);
		emit(event);
	}
	
	@Override
	public void createSubComponents() {

	}
	
	@Override
	public void fill(HawkeyeEvent event) {
		//always get new ID
		int id = random.nextInt(compClass.maxIDs);
		event.PacketID = compClass.prefix + id;
		event.TsIn = ProdUtils.getEventTime();
		event.TsOut = event.TsIn + random.nextInt(ProdUtils.MAX_PACKET_DELAY);
	}
	
	public void emit(HawkeyeEvent event) {
		//String json = gson.toJson(event);
		String json = event.toString();
		KeyedMessage<String, String> data = new KeyedMessage<String, String>
			(ProdUtils.hawkeyeTopic, ProdUtils.appID.toString(), json);
		ProdUtils.kafkaProducer.send(data);
		System.out.println(json);
	}
}

