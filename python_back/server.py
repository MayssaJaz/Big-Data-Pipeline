import json
from flask import Flask
from flask import jsonify
from flask import request
import happybase
from collections import OrderedDict
from flask_cors import CORS, cross_origin

app = Flask(__name__)
CORS(app, support_credentials=True)
connection = happybase.Connection('hadoop-master')
table = connection.table('crypto')

quarks = [{'name': 'up', 'charge': '+2/3'},
          {'name': 'down', 'charge': '-1/3'},
          {'name': 'charm', 'charge': '+2/3'},
          {'name': 'strange', 'charge': '-1/3'}]

@app.route('/', methods=['GET'])
@cross_origin(supports_credentials=True)
def hello_world():

    json_array=[]
    for key, data in table.scan():
       id=data.get(b'details:id').decode('utf-8')
       price=data.get(b'details:price').decode('utf-8')
       timestamp=data.get(b'details:timestamp').decode('utf-8')
       currency = {}
       currency['id'] = id
       currency['price']=price
       currency['timestamp']=timestamp
       json_data = json.dumps(currency)
       json_array.append(json_data)
    return jsonify({'message':json_array[-100:]})

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)