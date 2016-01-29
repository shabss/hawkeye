#!/usr/bin/python

import kafka
import logging
import random
import json
import time
from datetime import datetime
from time import time
import sys

TOT_SWTYPE 			= 100
TOT_SWID			= 100

TOT_TASKTYPE 		= 1000
TOT_TASKID			= 1000

TOT_HWTYPE			= 100
TOT_HWID			= 100000

TOT_PACKETID		= 100000

MAX_PACKET_DELAY	= 100000
MAX_MESSAGES		= 1000000

TOT_DEVICETYPE		= 1000
TOT_DEVICEID		= 1000

class hawkeyedat:
	AppID = ""
	
	SwID = ""
	SwType = ""
	
	TaskID = ""
	TaskType = ""
	
	HwID = ""
	HwType = ""
	
	DevID = ""
	DevType = ""
	
	PacketID = ""
	TsIn = 0
	TsOut = 0
	
	def __init__(self):
		pass
		
	def popApp(self):
		self.AppID = "HawkEye"
		
	def popSW(self):
		self.SwID = "SWID%s" % random.randint(0, TOT_SWID)
		self.SwType = "SWTYPE%s" % random.randint(0, TOT_SWTYPE)
		
	def popTask(self):
		self.TaskID = "TASKID%s" % random.randint(0, TOT_TASKID)
		self.TaskType = "TASKTYPE%s" % random.randint(0, TOT_TASKTYPE)

	def popHW(self):
		self.HwID = "HWID%s" % random.randint(0, TOT_HWID)
		self.HwType = "HWTYPE%s" % random.randint(0, TOT_HWTYPE)

	def popPacket(self):
		self.PacketID = "PACKET%s" % random.randint(0, TOT_PACKETID)
		self.TsIn = self.getNowMicrosec()
		self.TsOut = self.TsIn + random.randint(0, MAX_PACKET_DELAY)
		
	def popDevice(self):
		self.DevID = "DEVID%s" % random.randint(0, TOT_HWID)
		self.DevType = "DEVTYPE%s" % random.randint(0, TOT_HWTYPE)
	def getNowMicrosec(self):
		#To Do: Get consistent timestamp across all systems
		return int (round(time() * 1000000))
		
	
random.seed(3002)
cluster = kafka.KafkaClient("52.34.46.84:9092")
prod = kafka.SimpleProducer(cluster, async=True)
topic = "hawkeye4"
#topic = "hawkeye-test4"
doProd = True
msgCount = 0
msgBatch = []
minTime = datetime.min
#print len(sys.argv)
printOnly = 0
norandom = 0

argvOfs = 1
while argvOfs < len(sys.argv):
	if sys.argv[argvOfs] == "printonly":
		printOnly = 1
	elif sys.argv[argvOfs] == "norandom":
		norandom = 1
	argvOfs = argvOfs + 1

start_time = time()
while msgCount < MAX_MESSAGES:
	if norandom == 0:
		hmsg = hawkeyedat()
		hmsg.popApp()
		hmsg.popSW()
		hmsg.popHW()
		hmsg.popTask()
		hmsg.popDevice()
		hmsg.popPacket()
		hmsgjson = json.dumps(hmsg, default=lambda o: o.__dict__)
	else:
		hmsgjson = '{"DevID": "DEVID25438", "TsIn": 1453947387860804, "SwType": "SWTYPE68", "SwID": "SWID92", "TaskType": "TASKTYPE291", "HwID": "HWID20970", "HwType": "HWTYPE10", "DevType": "DEVTYPE86", "TsOut": 1453947387953349, "TaskID": "TASKID588", "AppID": "HawkEye", "PacketID": "PACKET90543"}'

	#print hmsgjson
	if printOnly == 0:
		if msgCount % 200 == 0 and msgCount != 0:
			prod.send_messages(topic, *msgBatch)
			msgBatch = []
		else:
			msgBatch.append(hmsgjson)

		if msgCount % 10000 == 0:
			print "Messages sent: %s" % msgCount
	else:
		print hmsgjson
	msgCount = msgCount + 1
	
	
end_time = time()
print "Total Time to push %s messages %s" % (msgCount, end_time - start_time)
