#!/usr/bin/python

from app import app
from flask import jsonify 
from app import app
from cassandra.cluster import Cluster
from flask import render_template

from redis import StrictRedis

redisdb = StrictRedis(host='54.148.25.241', port=6379, db=0)

USE_OLD_CLUSTER = 0
CASSANDRA_KEYSPACE = 'hawkeye4'
KAFKA_TOPIC = 'hawkeye4'

if USE_OLD_CLUSTER == 1:
	master_ip = "ip-172-31-2-168"
	master_public_dns = "ec2-52-34-46-84.us-west-2.compute.amazonaws.com"
	worker_public_dns = ['52.34.46.84', '52.89.61.14', '52.27.234.47', '52.24.233.165'] 
else :
	master_ip = "ip-172-31-2-180"
	master_public_dns = "'ec2-52-34-253-146.us-west-2.compute.amazonaws.com"
	worker_public_dns = ['52.34.253.146', '52.27.28.14', '52.35.88.14', '52.32.240.173', '52.88.31.138']
	
cluster = Cluster(worker_public_dns)
session = cluster.connect(CASSANDRA_KEYSPACE)
g_monitors = {}
   
@app.route('/api/email/<email>/<date>')
def get_email(email, date):
	stmt = "SELECT * FROM email WHERE id=%s and date=%s"
	response = session.execute(stmt, parameters=[email, date])
	response_list = []
	for val in response:
		response_list.append(val)
	jsonresponse = [{"first name": x.fname, "last name": x.lname, "id": x.id, "message": x.message, "time": x.time} for x in response_list]
	return jsonify(emails=jsonresponse)
	

@app.route('/')
@app.route('/index')
def index():
    return render_template("index.html")

@app.route('/monitors')
def monitors():
    return render_template("monitors.html")
	
@app.route('/api/stream/monitors/')
def report_monitors():
	user_monitors = ["hawkeye", "SWTYPE23", "SWID44", "TASKTYPE100", "TASKID100", "SWTYPE20", "SWID20", "TASKTYPE20", "TASKID20",  "SWID0"]
	monitors_through = {}
	for mon in user_monitors:
		monitors_through[mon] = redisdb.get(mon+'_now')
	jsonresponse = [{"monitor": key, "through": value} for key, value in monitors_through.iteritems()]
	print jsonresponse
	return jsonify(monitors=jsonresponse)


	