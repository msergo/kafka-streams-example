package org.msergo.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class WikiTimestampExtractor implements TimestampExtractor {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        try {
            JsonNode node = mapper.readTree((JsonParser) record.value());
            JsonNode tsNode = node.get("timestamp");
            if (tsNode != null && tsNode.isNumber()) {
                long ts = tsNode.asLong();
                if (ts < 10_000_000_000L) {
                    ts *= 1000;
                }
                return ts;
            }
        } catch (Exception ignored) {
        }
        // fallback to Kafka record timestamp
        return record.timestamp();
    }
}