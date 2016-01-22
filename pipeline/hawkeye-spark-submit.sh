 $SPARK_HOME/bin/spark-submit \
  --class <main-class>
  --master spark://ip-172-31-2-168:7077 \
  --deploy-mode <deploy-mode> \
  --conf <key>=<value> \
  ... # other options
  <application-jar> \
  [application-arguments]