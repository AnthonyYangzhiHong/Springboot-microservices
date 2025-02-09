package com.embarkx.reviewms.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReviewProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ReviewProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendReviewMessage(String companyId, double rating) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("companyId", companyId);
            message.put("rating", rating);
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("review-topic", jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 