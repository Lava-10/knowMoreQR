package com.knowMoreQR.server.service;

import com.openai.client.OpenAiClient;
import com.openai.client.models.requests.chat.ChatCompletionRequest;
import com.openai.client.models.requests.chat.Message;
import com.openai.client.models.responses.chat.ChatCompletionResponse;
import com.openai.client.impl.OpenAiClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class OpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);
    private final OpenAiClient openAiClient;

    public OpenAiService(@Value("${openai.api.key}") String apiKey) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY_HERE")) {
            logger.warn("OpenAI API Key is not configured. AI features will be disabled.");
            this.openAiClient = null; // Or handle differently, e.g., throw exception
        } else {
            logger.info("Initializing OpenAI client...");
            // Consider making timeout configurable
            this.openAiClient = new OpenAiClientImpl(apiKey, Duration.ofSeconds(60));
            logger.info("OpenAI client initialized successfully.");
        }
    }

    /**
     * Analyzes a natural language command related to wishlist management.
     * Tries to determine the intent (add/remove/view) and potential item criteria.
     *
     * @param command The natural language command from the user.
     * @return A string containing the AI's interpretation or response, or null if AI is disabled or fails.
     */
    public String analyzeWishlistCommand(String command) {
        if (openAiClient == null) {
            logger.warn("OpenAI client not available. Cannot analyze command: {}", command);
            return "AI processing is currently unavailable.";
        }

        // Construct a prompt for the ChatCompletion API
        // This prompt needs careful engineering based on desired output format
        String systemPrompt = "You are a helpful assistant managing a user's fashion wishlist. " +
                "Analyze the user's command and determine the intent (e.g., add, remove, view, list, clear) " +
                "and any specific item criteria (e.g., item name, color, brand, sustainability feature like 'low carbon footprint'). " +
                "Respond with a structured summary or confirmation. Example: Intent: add, Item: blue sweater, Criteria: cotton"; // Adjust this example as needed
        
        Message systemMessage = Message.builder().role("system").content(systemPrompt).build();
        Message userMessage = Message.builder().role("user").content(command).build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo") // Or another suitable model like gpt-4o-mini
                .messages(List.of(systemMessage, userMessage))
                .maxTokens(100) // Adjust as needed
                .temperature(0.5) // Adjust for creativity vs determinism
                .build();

        try {
            logger.debug("Sending command to OpenAI: {}", command);
            ChatCompletionResponse response = openAiClient.createChatCompletion(request);
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String result = response.getChoices().get(0).getMessage().getContent();
                logger.debug("Received response from OpenAI: {}", result);
                return result;
            } else {
                logger.error("Received no valid choices from OpenAI API.");
                return "Error processing command with AI.";
            }
        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return "Error processing command with AI.";
        }
    }

    // Add other methods for different AI tasks (e.g., OCR processing help, recommendations)
    // public String processImageWithOCR(byte[] imageData) { ... }
    // public String getShoppingRecommendations(String preferences) { ... }

} 