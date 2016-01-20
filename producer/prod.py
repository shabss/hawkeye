
import kafka
import logging

logging.basicConfig(
    format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:%(levelname)s:%(process)d:%(message)s',
    level=logging.DEBUG
)

cluster = kafka.KafkaClient("52.34.46.84:9092")
prod = kafka.SimpleProducer(cluster, async=False)
topic = "hawkeye-prod1"
msg_list = ["{msg : 'first message'}", "{msg: 'second message'}"]
prod.send_messages(topic, *msg_list)
