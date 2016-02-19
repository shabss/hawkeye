#!/usr/bin/python

from app import app
from flask import jsonify 
from app import app
from cassandra.cluster import Cluster
from flask import render_template

from redis import StrictRedis

CASSANDRA_KEYSPACE = 'hawkeye4'
KAFKA_TOPIC = 'hawkeye4'
REDIS_HOST = '54.148.25.241'
REDIS_PORT = 6379
REDIS_TIMEOUT = 1000

worker_public_dns = ['52.34.253.146', '52.27.28.14', '52.35.88.14', '52.32.240.173', '52.88.31.138']

redisdb = None
cluster = None
session = None

try:
	#if redisdb is None:
	redisdb = StrictRedis(host=REDIS_HOST, port=REDIS_PORT, db=0)
	redisdb.ping()
except:
	print "Unable to connect to redis %s:%s" % (REDIS_HOST, REDIS_PORT)
	redisdb = None

try:
	cluster = Cluster(worker_public_dns)
	session = cluster.connect(CASSANDRA_KEYSPACE)
except:
	print "Unable to connect to cassandra [%s] keyspace %s" % (worker_public_dns, CASSANDRA_KEYSPACE)
	cluster = None;
	session = None;
connectSuccess = {'redis' : redisdb is not None, 'cassandra': session is not None}
print connectSuccess

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

user_monitors = ["hawkeye", "mysql", "kafka", "SWTYPE23", "SWID44", "TASKTYPE100", "TASKID100", "SWTYPE20", "SWID20", "TASKTYPE20", "TASKID20",  "SWID0"]
	
@app.route('/api/stream/monitors/')
def report_monitors():
	monitors_through = {}
	for mon in user_monitors:
		monitors_through[mon] = redisdb.get(mon+'_now')

	jsonresponse = [{"monitor": key, "nowValue": value} for key, value in monitors_through.iteritems()]
	print jsonresponse
	return jsonify(monitors=jsonresponse)


@app.route('/api/stream/alerts/')
def report_alerts():
	alerts = {}
	stmt = """select monitor, alert_time_ms, alert_through, alert_sev, min_through, 
			sigma2neg_through, sigma1neg_through, sigma1pos_through, sigma2pos_through, max_through  
			from monitor_alerts where monitor in (%s) and alert_time_year = 2016 limit 25""" % ','.join(["'" + m + "'" for m in user_monitors])
	#print stmt
	response_list = []
	if session is not None:
		response = session.execute(stmt)
	else:
		response = []
		
	for val in response:
		response_list.append(val)
	jsonresponse = [{
		"monitor": x.monitor, "alert_time_ms": x.alert_time_ms, "alert_through": x.alert_through,
		"alert_sev": x.alert_sev, "min_through": x.min_through, "sigma2neg_through": x.sigma2neg_through, 
		"sigma1neg_through": x.sigma1neg_through, "sigma1pos_through": x.sigma1pos_through, 
		"sigma2pos_through": x.sigma2pos_through, "max_through" : x.max_through} for x in response_list]
	print jsonresponse
	return jsonify(alerts=jsonresponse)
	