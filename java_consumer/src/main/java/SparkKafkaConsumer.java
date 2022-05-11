import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import java.util.ArrayList;

import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
public class SparkKafkaConsumer {
    private static Table table1;
    private static String tableName = "crypto";
    private static String family1 = "details";
    public static void storeInHbase( String id, String price, String timestamp)  throws IOException {

        Configuration config = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(config);
        Admin admin = connection.getAdmin();

        table1 = connection.getTable(TableName.valueOf(tableName));

        try {
            System.out.println("Adding crypto");
            byte[] row1 = Bytes.toBytes(id + timestamp);
            Put p = new Put(row1);

            p.addColumn(family1.getBytes(), "id".getBytes(), Bytes.toBytes(id));
            p.addColumn(family1.getBytes(), "price".getBytes(), Bytes.toBytes(price));
            p.addColumn(family1.getBytes(), "timestamp".getBytes(), Bytes.toBytes(timestamp));
            table1.put(p);
        } catch (Exception e) {
            table1.close();
            connection.close();

        }

    }

    private SparkKafkaConsumer() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: SparkKafkaWordCount <zkQuorum> <group> <topics> <numThreads>");
            System.exit(1);
        }

        SparkConf sparkConf = new SparkConf().setAppName("SparkKafkaWordCount");
        // Creer le contexte avec une taille de batch de 2 secondes
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,
                new Duration(30000));

        int numThreads = Integer.parseInt(args[3]);
        Map<String, Integer> topicMap = new HashMap<>();
        String[] topics = args[2].split(",");
        for (String topic: topics) {
            topicMap.put(topic, numThreads);
        }

        JavaPairReceiverInputDStream<String, String> messages =
                KafkaUtils.createStream(jssc, args[0], args[1], topicMap);

        JavaDStream<String> lines = messages.map(Tuple2::_2);


        JavaDStream<Object> wordCounts =lines.map(d ->{
            ArrayList<String> currencies = new ArrayList<String>();
            JSONParser parse = new JSONParser();
            JSONArray array = (JSONArray) parse.parse(d);
            for(int i=0;i<array.size();i++) {
                JSONObject obj = (JSONObject) array.get(i);
                System.out.println("id==="+obj.get("id"));
                System.out.println("price==="+obj.get("price"));
                System.out.println("price_timestamp==="+obj.get("price_timestamp"));
                storeInHbase( (String) obj.get("id"), (String) obj.get("price"), (String) obj.get("price_timestamp"));
                d = obj.toJSONString();
                currencies.add(d);
            }

            return currencies;

        } );
        wordCounts.print();
        jssc.start();
        jssc.awaitTermination();
    }
}
