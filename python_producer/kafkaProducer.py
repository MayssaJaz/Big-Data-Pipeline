from kafka import KafkaProducer
from json import dumps
import threading
import json
import requests

producer = KafkaProducer(bootstrap_servers='hadoop-master:9092', value_serializer=lambda x: dumps(x).encode('utf-8'))


def getdata():
    threading.Timer(30.0, getdata).start()
    url = requests.get("https://api.nomics.com/v1/currencies/ticker?key=690b086552758cf1e34cbcba2890ae568f822625")
    text = url.text
    data = json.loads(text)
    producer.send('crypto', value=data)

getdata()