package pl.edu.agh.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableBinding(Source.class)
@EnableScheduling
public class SourceApplication {

    private static final Random random = new Random();
    private final Source source;

    @Autowired
    public SourceApplication(Source source) {
        this.source = source;
    }

    public static void main(String[] args) {
        SpringApplication.run(SourceApplication.class, args);
    }

    private static String generateRandomText() {
        return random
                .ints(random.nextInt(1000), 'a', 'z' + 1)
                .mapToObj(x -> String.valueOf((char) x))
                .collect(Collectors.joining(" "));
    }

    private static String generateRandomNumbers() {
        double d = -1000 + 2000 * random.nextDouble();
        System.out.println("Sending " +d);
        return d+"";
    }

    @Scheduled(fixedRate = 1)
    private void send() {
        source.output().send(MessageBuilder.withPayload(
//                generateRandomText()
                generateRandomNumbers()
        ).build());
    }
}
