package pl.edu.agh;

import org.apache.flink.api.common.functions.MapFunction;

public class WordsCounter implements MapFunction<String, String> {
    private long wordsCounter = 0;

    @Override
    public String map(String value) throws Exception {
        long wordsAmount = value.split(" ").length;
        wordsCounter += wordsAmount;
        String result = "Words in message: " + wordsAmount + ", in total: " + wordsCounter;
        return result;
    }
}
