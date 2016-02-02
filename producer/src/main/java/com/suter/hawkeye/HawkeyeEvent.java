
package com.suter.hawkeye;
 
import java.io.Serializable;

/*{
	"DevID": "DEVID25438", 
	"TsIn": 1453947387860804, 
	"SwType": "SWTYPE68", 
	"SwID": "SWID92", 
	"TaskType": "TASKTYPE291", 
	"HwID": "HWID20970", 
	"HwType": "HWTYPE10", 
	"DevType": "DEVTYPE86", 
	"TsOut": 1453947387953349, 
	"TaskID": "TASKID588", 
	"AppID": "HawkEye", 
	"PacketID": "PACKET90543"
}*/

public class HawkeyeEvent implements Serializable {
	public long TsIn;
	public long TsOut;
	public String PacketID;
	//monitors
	public String AppID;
	public String SwType;
	public String SwID;
	public String TaskType;
	public String TaskID;
	/*
	public String HwType;
	public String HwID;
	public String DevType;
	public String DevID;
	*/
	
	public HawkeyeEvent() {
		
	}
	
	public HawkeyeEvent(long TsIn, long TsOut, String AppID, String PacketID,
						String SwType, String SwID, 
						String TaskType, String TaskID
						/*, String HwType, String HwID,
						String DevType, String DevID*/
						) {
		this.TsIn = TsIn ;
		this.TsOut = TsOut;
		this.AppID = AppID;
		this.PacketID = PacketID;
		this.SwType = SwType;
		this.SwID = SwID;
		this.TaskType = TaskType;
		this.TaskID = TaskID;
		/*
		this.HwType = HwType;
		this.HwID = HwID;
		this.DevType = DevType;
		this.DevID = DevID;
		*/
  }

  @Override
  public String toString() {
    return "HawkeyeEvent {" +
        "TsIn=" + TsIn +
        ", TsOut=" + TsOut +
        ", AppID='" + AppID + '\'' +
        ", PacketID='" + PacketID + '\'' +
        ", SwType='" + SwType + '\'' +
        ", SwID='" + SwID + '\'' +		
        ", TaskType='" + TaskType + '\'' +
		", TaskID='" + TaskID + '\'' +
		/*
		", HwType='" + HwType + '\'' +
		", HwID='" + HwID + '\'' +
		", DevType='" + DevType + '\'' +
		", DevID='" + DevID + '\'' +
		*/
        '}';
  }
}



