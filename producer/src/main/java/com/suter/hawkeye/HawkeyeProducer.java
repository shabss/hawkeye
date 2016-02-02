package com.suter.hawkeye;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class HawkeyeProducer 
{
	public static final Logger LOG = LoggerFactory.getLogger(HawkeyeProducer.class);
    public static void main( String[] args )
    {
        System.out.println( "Producer Started" );
		parseArgs(args);
		
		ProdUtils.packetCompClass = new ClassPacket();
		ProdUtils.taskCompClass = new ClassTask();
		ProdUtils.swCompClass = new ClassSoftware();
		ProdUtils.appCompClass = new ClassApp();
		
		ProdUtils.packetCompClass.init();
		ProdUtils.taskCompClass.init();
		ProdUtils.swCompClass.init();
		ProdUtils.appCompClass.init();
		
		AppComponent app = (AppComponent) ProdUtils.appCompClass.createEventComponent
			(null, new Integer(0), ProdUtils.appID, "APP", ProdUtils.appName);
		
		app.createSubComponents();
		app.init();
		app.startEmit();
		
		//wait for app to finish;
    }
	
	public static void parseArgs(String [] args) {
		ProdUtils.appID = new Integer(0);
		ProdUtils.appName = "hawkeye";
	}
}


