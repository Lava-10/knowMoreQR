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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@Service
public class OpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);
    private final OpenAiClient openAiClient;
    private final Gson gson = new Gson();

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
     * Analyzes a natural language command related to wishlist management using OpenAI.
     * Returns a structured analysis object (ParsedCommand) containing intent and item query.
     *
     * @param command The natural language command from the user.
     * @return A ParsedCommand object, or null if AI is disabled or parsing fails.
     */
    public ParsedCommand analyzeWishlistCommandStructured(String command) {
        if (openAiClient == null) {
            logger.warn("OpenAI client not available. Cannot analyze command: {}", command);
            return new ParsedCommand("error", null, "AI processing is currently unavailable.");
        }

        String systemPrompt = "You are an assistant managing a user's fashion wishlist. " +
                "Analyze the user's command to determine the primary intent (add, remove, view, clear) " +
                "and the target item or query if applicable. " +
                "Respond ONLY with a JSON object containing two fields: 'intent' (string: add, remove, view, clear, or unknown) " +
                "and 'item_query' (string: the identified item name/description, or empty string if not applicable/found). " +
                "Example command 'add the blue sweater', respond: {\"intent\": \"add\", \"item_query\": \"blue sweater\"}. " +
                "Example command 'show my list', respond: {\"intent\": \"view\", \"item_query\": \"\"}. " +
                "Example command 'get rid of shirts', respond: {\"intent\": \"remove\", \"item_query\": \"shirts\"}.";

        Message systemMessage = Message.builder().role("system").content(systemPrompt).build();
        Message userMessage = Message.builder().role("user").content(command).build();

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo-0125") // Ensure model supports JSON mode if available/needed
                // Optional: Enable JSON mode if supported by the model and library version
                // .responseFormat(ChatCompletionResponseFormat.builder().type("json_object").build()) 
                .messages(List.of(systemMessage, userMessage))
                .maxTokens(150) // Adjust as needed for JSON response
                .temperature(0.2) // Lower temperature for more deterministic JSON output
                .build();

        try {
            logger.debug("Sending command to OpenAI for structured analysis: {}", command);
            ChatCompletionResponse response = openAiClient.createChatCompletion(request);
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String jsonResponse = response.getChoices().get(0).getMessage().getContent();
                logger.debug("Received raw JSON response from OpenAI: {}", jsonResponse);
                
                // Attempt to parse the JSON response
                try {
                    // Basic cleanup in case the model includes markdown backticks
                    jsonResponse = jsonResponse.trim().replace("```json", "").replace("```", "").trim(); 
                    JsonObject parsedJson = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    String intent = parsedJson.has("intent") ? parsedJson.get("intent").getAsString() : "unknown";
                    String itemQuery = parsedJson.has("item_query") ? parsedJson.get("item_query").getAsString() : "";
                    return new ParsedCommand(intent, itemQuery, null); // Success
                } catch (JsonSyntaxException | IllegalStateException | ClassCastException e) {
                    logger.error("Failed to parse JSON response from OpenAI: {}. Error: {}", jsonResponse, e.getMessage());
                    return new ParsedCommand("error", null, "Failed to parse AI response.");
                }
            } else {
                logger.error("Received no valid choices from OpenAI API.");
                return new ParsedCommand("error", null, "No response from AI.");
            }
        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return new ParsedCommand("error", null, "Error calling AI service.");
        }
    }

    // Simple inner class to hold the parsed command structure
    public static class ParsedCommand {
        private final String intent;
        private final String itemQuery;
        private final String errorMessage; // Only populated on error

        public ParsedCommand(String intent, String itemQuery, String errorMessage) {
            this.intent = intent != null ? intent : "unknown";
            this.itemQuery = itemQuery != null ? itemQuery : "";
            this.errorMessage = errorMessage;
        }

        public String getIntent() { return intent; }
        public String getItemQuery() { return itemQuery; }
        public String getErrorMessage() { return errorMessage; }
        public boolean hasError() { return errorMessage != null; }
    }

    // Add other methods for different AI tasks (e.g., OCR processing help, recommendations)
    // public String processImageWithOCR(byte[] imageData) { ... }
    // public String getShoppingRecommendations(String preferences) { ... }

} 