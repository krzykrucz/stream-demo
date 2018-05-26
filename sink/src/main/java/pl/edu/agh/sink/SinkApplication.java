package pl.edu.agh.sink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import java.nio.file.Files;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

@SpringBootApplication
@EnableBinding(Sink.class)
public class SinkApplication {

    private static final long WINDOW_LENGTH_MILLIS = 5 * 1000; // 5 seconds
    private long nextWindowEndTime = 0;
    private long receivedMessages = 0;
    
    public static void main(String[] args) {
        SpringApplication.run(SinkApplication.class, args);
    }

    @PostConstruct
    public void init() throws Exception {
        nextWindowEndTime = new Date().getTime() + WINDOW_LENGTH_MILLIS;
        receivedMessages = 0;
    }
    
    @StreamListener(Sink.INPUT)
    void receive(Message<String> message) throws Exception {
        System.out.println("Received " + message.getPayload());

        receivedMessages++;

        long timeStamp = new Date().getTime();
        if(timeStamp >= nextWindowEndTime) {
            Files.write(Paths.get("results.txt"),
                        (timeStamp + "," +receivedMessages + "\n").getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            receivedMessages = 0;
            nextWindowEndTime = timeStamp + WINDOW_LENGTH_MILLIS;
        }
    }
}
