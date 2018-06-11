package pl.edu.agh.sparkprocessor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.codehaus.jackson.map.ObjectMapper;
import scala.Tuple2;

import java.util.*;

public class WordCountProcessor {

    private static final String LOG_LEVEL = "WARN";
    private static final String RESULT_TOPIC = "my-proc";
    private static final Duration BATCH_DURATION = Durations.seconds(5);

    public static void main(String[] args) throws InterruptedException {
        final String brokers = getEnvOrExitWithError("BROKERS");
        final String topics = getEnvOrExitWithError("TOPICS");

        final JavaStreamingContext jssc = setupSpark("WordCountProcessor");
        final JavaInputDStream<ConsumerRecord<String, String>> messages = createKafkaMessageStream(jssc, brokers, topics);

        final Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaParams.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        kafkaParams.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        //wordCount(brokers, messages. kafkaParams);
        linearRegression(brokers, messages, kafkaParams);

        jssc.start();
        jssc.awaitTermination();
    }

	private static void wordCount(final String brokers,
			final JavaInputDStream<ConsumerRecord<String, String>> messages, final Map<String, Object> kafkaParams) {
		final JavaPairDStream<String, Integer> wordCounts = messages
                .map(ConsumerRecord::value)
                .flatMap(x -> Arrays.asList(x.split("\\s+")).iterator())
                .mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);

        wordCounts.foreachRDD(rdd -> {
            String resultMessage = new ObjectMapper().writeValueAsString(rdd.collectAsMap());
            System.out.println(resultMessage);

            KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaParams);

            producer.send(new ProducerRecord<>(RESULT_TOPIC, resultMessage));
            producer.close();
        });
	}
	
	private static void linearRegression(final String brokers,
			final JavaInputDStream<ConsumerRecord<String, String>> messages, final Map<String, Object> kafkaParams) {
		
		final JavaDStream<String> numberCounts = messages
				.map(ConsumerRecord::value).flatMap(x -> Arrays.asList(x).iterator());
		
		numberCounts.foreachRDD(rdd -> {
			List<String> numbers = rdd.collect();
			for(String number : numbers)
			{
                pl.edu.agh.sparkprocessor.LinearRegression.addNum(Double.parseDouble(number));
                String resultMessage = pl.edu.agh.sparkprocessor.LinearRegression.regression();

                KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaParams);
                producer.send(new ProducerRecord<>(RESULT_TOPIC, resultMessage));
                producer.close();
            }
		});	
	}
	

    private static String getEnvOrExitWithError(String name) {
        String env = System.getenv(name);
        if(name.equals("BROKERS")) env = "localhost:9092";
        else env = "my-source";
        if (env == null) {
            System.err.println("\"" + name + "\" env must be provided");
            System.exit(1);
        }
        return env;
    }

    private static JavaStreamingContext setupSpark(String appName) {
        final SparkConf sparkConf = new SparkConf()
                .setAppName(appName)
                .setMaster("local[2]");

        final JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, BATCH_DURATION);
        final JavaSparkContext sc = jssc.sparkContext();
        sc.setLogLevel(LOG_LEVEL);

        return jssc;
    }

    private static JavaInputDStream<ConsumerRecord<String, String>> createKafkaMessageStream(
            JavaStreamingContext jssc,
            String brokers,
            String topics
    ) {
        final Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));

        final Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "blabla");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topicsSet, kafkaParams)
        );
    }
}

