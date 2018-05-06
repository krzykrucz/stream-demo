package pl.edu.agh.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableBinding(Source.class)
@EnableScheduling
public class SourceApplication {

    @Autowired
    Source source;

    public static void main(String[] args) {
        SpringApplication.run(SourceApplication.class, args);
    }

    @Scheduled(fixedRate = 1000)
    void send() {
        source.output()
                .send(MessageBuilder.withPayload("text").build());
    }
}
