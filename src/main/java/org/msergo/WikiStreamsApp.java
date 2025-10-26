package org.msergo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.msergo.utils.JsonUtils;
import org.msergo.utils.WikiTimestampExtractor;

import java.util.Properties;

public class WikiStreamsApp {


    private static final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wiki-streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WikiTimestampExtractor.class); // Get timestamps from wiki data, not from Kafka
        props.put("auto.offset.reset", "earliest"); // Do not commit offsets


        StreamsBuilder builder = new StreamsBuilder();


        KStream<String, String> source = builder.stream("wikipedia-changes", Consumed.with(Serdes.String(), Serdes.String()));

        source
                .filter((key, value) -> {
                    JsonNode node = JsonUtils.parse(value);
                    return !JsonUtils.getBoolean(node, "bot"); // Filter out bot edits
                })
                .foreach((key, value) -> {
            try {
                JsonNode node = mapper.readTree(value);
                String title = JsonUtils.getText(node, "title", "<no title>");
                System.out.println("Changed article: " + title);
            } catch (Exception e) {
                System.err.println("Failed to parse JSON: " + e.getMessage());
            }
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
    }
}