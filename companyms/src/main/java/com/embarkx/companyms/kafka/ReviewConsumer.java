package com.embarkx.companyms.kafka;

import com.embarkx.companyms.company.Company;
import com.embarkx.companyms.company.CompanyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReviewConsumer {
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    public ReviewConsumer(CompanyRepository companyRepository, ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "review-topic", groupId = "company-group")
    public void listenReviewMessages(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            Long companyId = Long.parseLong(data.get("companyId").toString());
            double rating = Double.parseDouble(data.get("rating").toString());

            Company company = companyRepository.findById(companyId).orElse(null);
            if (company != null) {
                double newAvgRating = calculateNewAvgRating(company, rating);
                company.setAvgRating(newAvgRating);
                companyRepository.save(company);
            }
        } catch (Exception e) {
            System.err.println("Error parsing message: " + e.getMessage());
            // Handle the error, e.g., log it or send to a dead-letter queue
        }
    }

    private double calculateNewAvgRating(Company company, double newRating) {
        Double currentAvgRating = company.getAvgRating();
        if (currentAvgRating == null) {
            return newRating;
        } else {
            // Implement logic to calculate new average rating
            // For simplicity, assume avgRating is recalculated based on newRating
            return (currentAvgRating + newRating) / 2; // Simplified example
        }
    }
} 