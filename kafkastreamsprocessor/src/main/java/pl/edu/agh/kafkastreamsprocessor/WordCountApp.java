package pl.edu.agh.kafkastreamsprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordCountApp {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WordCountApp.class, args);
    }
}
