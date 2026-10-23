package com.carebridge.carebridge.Service;

import com.carebridge.carebridge.Enum.IntentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
@Service
public class IntentClassificationService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public IntentClassificationService(
            @Value("${mercury.api-url}") String apiUrl,
            @Value("${mercury.api-key}") String apiKey,
            @Value("${mercury.model}") String model,
            ObjectMapper objectMapper) {

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .build();

        this.objectMapper = objectMapper;
        this.model = model;
    }

    public IntentType classify(String message) {

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Message cannot be empty"
            );
        }

        Map<String, Object> criteria = Map.of(
                "RECORDS",
                "Questions requiring the user's personal medical records, diagnoses, prescriptions, test results, or medical history.",

                "HEALTH",
                "General health education, symptoms, wellness advice, or medical explanations that do not require personal records.",

                "GENERAL",
                "Casual conversation, greetings, unrelated questions, or help using the CareBridge application."
        );

        Map<String, Object> question = Map.of(
                "type", "choice",
                "instructions",
                "Classify the user's primary intent. Choose exactly the best category.",
                "criteria", criteria
        );

        Map<String, Object> request = Map.of(
                "model", model,
                "state", "User message: " + message,
                "questions", Map.of("team", question)
        );

        try {
            String response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            String choice = root.path("answers")
                    .path("team")
                    .path("choice")
                    .asText("");

            return IntentType.valueOf(choice);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}