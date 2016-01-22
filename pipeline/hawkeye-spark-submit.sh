 $SPARK_HOME/bin/spark-submit \
  --master spark://ip-172-31-2-168:7077 \
  --deploy-mode client \
  /home/ubuntu/projects/hawkeye/pipeline/hawkeye-batch.py 