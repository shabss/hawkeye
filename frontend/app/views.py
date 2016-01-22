#!/usr/bin/python

from app import app
from flask import jsonify 
from app import app
from cassandra.cluster import Cluster

cluster = Cluster(['52.34.46.84', '52.89.61.14', '52.27.234.47', '52.24.233.165']) 
session = cluster.connect('hawkeye3') 

@app.route('/')
@app.route('/index')
def index():
	return "Hello, World!"
   
@app.route('/api/email/<email>/<date>')
def get_email(email, date):
	stmt = "SELECT * FROM email WHERE id=%s and date=%s"
	response = session.execute(stmt, parameters=[email, date])
	response_list = []
	for val in response:
		response_list.append(val)
	jsonresponse = [{"first name": x.fname, "last name": x.lname, "id": x.id, "message": x.message, "time": x.time} for x in response_list]
	return jsonify(emails=jsonresponse)
	

@app.route('/api/batch/top/<top>/')
def get_top_monitors_batch(top):
	#stmt = "SELECT * FROM sliding_window_batch ORDER BY time_total DESC LIMIT %s"
	stmt = "SELECT * FROM sliding_window_batch LIMIT %s" % top
	response = session.execute(stmt)
	response_list = []
	for val in response:
		response_list.append(val)
	jsonresponse = [{"monitor": x.monitor, "ts_start": x.ts_start, "time_total": x.time_total, "event_count": x.event_count} for x in response_list]
	return jsonify(monitors=jsonresponse)
	
