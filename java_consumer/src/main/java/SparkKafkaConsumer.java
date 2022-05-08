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
import scala.tools.scalap.Main;

public class SparkKafkaConsumer {

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
                d = obj.toJSONString();
                currencies.add(d);
                System.out.println(d);
            }
            return currencies;

        } );
        wordCounts.print();

        jssc.start();
        jssc.awaitTermination();
    }
}

