#!/bin/bash

export PYTHONPATH=$(pwd)
CLUSTER_NAME=shabbir-hawkey

#./ec2spinup hawkeye_instance.json

./ec2fetch $REGION $CLUSTER_NAME

./ec2install $CLUSTER_NAME environment
./ec2install $CLUSTER_NAME ssh
./ec2install $CLUSTER_NAME aws
./ec2install $CLUSTER_NAME hadoop
./ec2install $CLUSTER_NAME zookeeper
#./ec2install $CLUSTER_NAME hive
#./ec2install $CLUSTER_NAME pig
./ec2install $CLUSTER_NAME kafka
#install camus : https://sites.google.com/a/insightdatascience.com/dataengineering/devsetups/camus-dev
./ec2install $CLUSTER_NAME spark
./ec2install $CLUSTER_NAME cassandra
./ec2install $CLUSTER_NAME redis

#on each node:
#	pip install cassandra-driver
#on frontend node:
#	pip install flask-socketio


export REGION=us-west-2
export CLUSTER_NAME=shabbir-hspc
./ec2fetch $REGION $CLUSTER_NAME
./ec2install $CLUSTER_NAME environment
./ec2install $CLUSTER_NAME ssh
./ec2install $CLUSTER_NAME aws
./ec2install $CLUSTER_NAME hadoop
./ec2install $CLUSTER_NAME spark
./ec2install $CLUSTER_NAME cassandra
./ec2install $CLUSTER_NAME opscenter
#install camus : https://sites.google.com/a/insightdatascience.com/dataengineering/devsetups/camus-dev

export CLUSTER_NAME=shabbir-kstz
./ec2fetch $REGION $CLUSTER_NAME
./ec2install $CLUSTER_NAME environment
./ec2install $CLUSTER_NAME ssh
./ec2install $CLUSTER_NAME aws
./ec2install $CLUSTER_NAME zookeeper
./ec2install $CLUSTER_NAME kafka
./ec2install $CLUSTER_NAME storm
./ec2install $CLUSTER_NAME redis


#Frontend
https://sites.google.com/a/insightdatascience.com/dataengineering/devsetups/flask-dev
sudo pip install cassandra-driver
sudo pip install flask-socketio

