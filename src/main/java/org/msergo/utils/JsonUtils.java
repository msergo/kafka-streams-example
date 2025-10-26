package org.msergo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Safely parse a JSON string into a JsonNode.
     * Returns null if parsing fails.
     */
    public static JsonNode parse(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get a boolean field value safely.
     * Returns false if the field is missing or not boolean.
     */
    public static boolean getBoolean(JsonNode node, String fieldName) {
        if (node == null) return false;
        JsonNode field = node.get(fieldName);
        return field != null && field.isBoolean() && field.asBoolean();
    }

    /**
     * Get a text field safely.
     * Returns a fallback if missing.
     */
    public static String getText(JsonNode node, String fieldName, String fallback) {
        if (node == null) return fallback;
        JsonNode field = node.get(fieldName);
        return field != null && field.isTextual() ? field.asText() : fallback;
    }
}