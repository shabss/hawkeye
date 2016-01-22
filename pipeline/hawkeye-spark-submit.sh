export PYTHONPATH=$SPARK_HOME/python:$SPARK_HOME/python/build:$SPARK_HOME/python/lib/py4j-0.8.2.1-src.zip:$PYTHONPATH
/home/ubuntu/projects/hawkeye/pipeline/hawkeye-batch.py

# $SPARK_HOME/bin/spark-submit \
#  --master spark://ip-172-31-2-168:7077 \
#  --deploy-mode client \
#  /home/ubuntu/projects/hawkeye/pipeline/hawkeye-batch.py 