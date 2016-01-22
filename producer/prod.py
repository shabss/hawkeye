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
		
	def getNowMicrosec(self):
		#To Do: Get consistent timestamp across all systems
		return int (round(time() * 1000000))
		
	
random.seed(3002)
cluster = kafka.KafkaClient("52.34.46.84:9092")
prod = kafka.SimpleProducer(cluster, async=False)
topic = "hawkeye-prod1"
doProd = True
msgCount = 0
minTime = datetime.min
printOnly = sys.argv[1] == "printonly"

while msgCount < MAX_MESSAGES:
	hmsg = hawkeyedat()
	hmsg.popApp()
	hmsg.popSW()
	hmsg.popHW()
	hmsg.popTask()
	hmsg.popPacket()
	hmsgjson = json.dumps(hmsg, default=lambda o: o.__dict__)
	#print hmsgjson
	if printOnly == 0:
		prod.send_messages(topic, hmsgjson)
		if msgCount % 10000 == 0:
			print "Messages sent: %s" % msgCount
	else:
		print hmsgjson
	msgCount = msgCount + 1
	 
#msg_list = ["{msg : 'first message'}", "{msg: 'second message'}"]
#prod.send_messages(topic, *msg_list)
