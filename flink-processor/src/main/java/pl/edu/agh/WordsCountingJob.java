package pl.edu.agh;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
public class WordsCountingJob {

    @PostConstruct
    public void init() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = setProperties();

        env.addSource(new FlinkKafkaConsumer011<String>(
                "my-source", new SimpleStringSchema(), properties))
                .map(new WordsCounter())
                .addSink(new FlinkKafkaProducer011<String>(
                        "localhost:9092",      // Kafka broker host:port
                        "my-proc",       // Topic to write to
                        new SimpleStringSchema()));

        env.execute("Flink Words Counter");
    }

    private Properties setProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("zookeeper.connect", "localhost:2181");
        properties.setProperty("group.id", "my-topic");

        return properties;
    }

}
