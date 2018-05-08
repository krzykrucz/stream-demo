package flinkQ;


import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import java.util.Date;
import java.util.Properties;

public class FlinkProcessor {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "my-topic");
        env.addSource(new FlinkKafkaConsumer010<String>("my-source", new SimpleStringSchema(), properties))
                .map(t -> "==[[" + t + "]]== " + new Date())
                .addSink(new FlinkKafkaProducer011<String>(
                        "localhost:9092",      // Kafka broker host:port
                        "my-proc",       // Topic to write to
                        new SimpleStringSchema()));

        env.execute("xD");
    }
}

