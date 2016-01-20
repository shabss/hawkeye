
import kafka
cluster = kafka.KafkaClient("172-31-2-168:9092")
prod = kafka.SimpleProducer(cluster, async=False)
topic = "pipeline-setup"
msg_list = ["first message", "second message"]
prod.send(topic, *msg_list)
