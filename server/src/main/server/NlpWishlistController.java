package com.knowMoreQR.server;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.google.gson.*;
import com.knowMoreQR.server.auth.NlpWishlistRequest;
import com.knowMoreQR.server.auth.NlpWishlistResponse;
import com.knowMoreQR.server.service.OpenAiService;
import com.knowMoreQR.server.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/nlp")
@CrossOrigin(origins = "*")
public class NlpWishlistController {

    private static final Logger logger = LoggerFactory.getLogger(NlpWishlistController.class);

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Value("${ASTRA_DB_ID}")
    private String astraDbId;
    @Value("${ASTRA_DB_REGION}")
    private String astraDbRegion;
    @Value("${ASTRA_DB_KEYSPACE}")
    private String astraDbKeyspace;
    @Value("${ASTRA_DB_APPLICATION_TOKEN}")
    private String astraDbToken;

    private final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private WishlistService wishlistService;

    // Patterns for parsing AI response (VERY basic, needs improvement)
    private static final Pattern INTENT_PATTERN = Pattern.compile("Intent:\\s*(\w+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ITEM_PATTERN = Pattern.compile("Item:\\s*(.*?)(?:,|$)", Pattern.CASE_INSENSITIVE);
    // Add more patterns for criteria if needed

    @PostMapping("/wishlist")
    public ResponseEntity<?> processWishlistCommand(@RequestBody NlpWishlistRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }

        // Assuming email is the username. Need ConsumerLogin ID.
        // TODO: Need a way to map email from UserDetails back to ConsumerLogin ID.
        // This might require adding ConsumerLogin ID to UserDetails or fetching it.
        // For now, let's assume a placeholder ID. Replace with real logic.
        Long consumerId = getCurrentConsumerId(authentication); 
        if (consumerId == null) {
             return ResponseEntity.status(500).body("Could not determine consumer ID.");
        }

        if (request == null || request.getCommand() == null || request.getCommand().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Command cannot be empty.");
        }

        logger.info("Received wishlist command: \"{}\" for consumer ID: {}", request.getCommand(), consumerId);

        String aiAnalysisResult = openAiService.analyzeWishlistCommand(request.getCommand());
        logger.info("AI analysis result: {}", aiAnalysisResult);

        // --- Basic AI Response Parsing --- 
        String intent = parseValue(INTENT_PATTERN, aiAnalysisResult, "unknown");
        String itemQuery = parseValue(ITEM_PATTERN, aiAnalysisResult, "");
        
        logger.info("Parsed Intent: {}, Item Query: {}", intent, itemQuery);

        NlpWishlistResponse response = new NlpWishlistResponse();
        response.setAiAnalysis(aiAnalysisResult);
        response.setSuccess(false); // Default to false

        try {
            switch (intent.toLowerCase()) {
                case "add":
                    if (itemQuery.isEmpty()) {
                        response.setMessage("Please specify which item to add.");
                        break;
                    }
                    // Find potential tags matching the query
                    List<Tag> foundTags = wishlistService.findTagsByName(itemQuery);
                    if (foundTags.isEmpty()) {
                        response.setMessage("Sorry, I couldn't find any items matching '" + itemQuery + "'.");
                    } else if (foundTags.size() > 1) {
                         // TODO: Handle multiple matches - maybe ask user to clarify?
                        response.setMessage("Found multiple items matching '" + itemQuery + "'. Please be more specific. Adding the first one for now.");
                        wishlistService.addItem(consumerId, foundTags.get(0).getId());
                        response.setSuccess(true);
                    } else {
                        wishlistService.addItem(consumerId, foundTags.get(0).getId());
                        response.setMessage("Added '" + foundTags.get(0).getName() + "' to your wishlist.");
                        response.setSuccess(true);
                    }
                    break;

                case "remove":
                     if (itemQuery.isEmpty()) {
                        response.setMessage("Please specify which item to remove.");
                        break;
                    }
                    // Find item(s) in current wishlist matching query
                    List<Tag> currentWishlist = wishlistService.getWishlistTags(consumerId);
                    List<Tag> tagsToRemove = currentWishlist.stream()
                            .filter(tag -> (tag.getName() != null && tag.getName().toLowerCase().contains(itemQuery.toLowerCase())))
                            .collect(java.util.stream.Collectors.toList());
                            
                    if (tagsToRemove.isEmpty()) {
                        response.setMessage("Couldn't find '" + itemQuery + "' in your wishlist.");
                    } else if (tagsToRemove.size() > 1) {
                         // TODO: Handle multiple matches - ask user?
                        response.setMessage("Found multiple items matching '" + itemQuery + "'. Please be more specific. Removing the first one for now.");
                        wishlistService.removeItem(consumerId, tagsToRemove.get(0).getId());
                        response.setSuccess(true);
                    } else {
                         wishlistService.removeItem(consumerId, tagsToRemove.get(0).getId());
                         response.setMessage("Removed '" + tagsToRemove.get(0).getName() + "' from your wishlist.");
                         response.setSuccess(true);
                    }
                    break;

                case "view":
                case "list":
                case "show":
                    response.setWishlistItems(wishlistService.getWishlistTags(consumerId));
                    response.setMessage("Here is your current wishlist.");
                    response.setSuccess(true);
                    break;

                case "clear":
                case "empty":
                    wishlistService.clearWishlist(consumerId);
                    response.setMessage("Your wishlist has been cleared.");
                    response.setSuccess(true);
                    break;

                default:
                    response.setMessage("Sorry, I couldn't understand that command fully. The AI analysis was: " + aiAnalysisResult);
                    break;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error processing wishlist command for consumer {}: {}", consumerId, e.getMessage());
            response.setMessage("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing wishlist command for consumer {}", consumerId, e);
            response.setMessage("An unexpected error occurred.");
        }
        
        // Optionally refresh wishlist items in response for add/remove actions too
        if(response.isSuccess() && response.getWishlistItems() == null && !intent.equalsIgnoreCase("clear")) {
             response.setWishlistItems(wishlistService.getWishlistTags(consumerId));
        }

        return ResponseEntity.ok(response);
    }

    private String parseValue(Pattern pattern, String text, String defaultValue) {
        if (text == null) return defaultValue;
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return defaultValue;
    }
    
    // Helper method to get ConsumerLogin ID (Needs proper implementation)
    private Long getCurrentConsumerId(Authentication authentication) {
        // --- THIS IS A PLACEHOLDER --- 
        // You need a reliable way to get the ConsumerLogin ID from the Authentication principal.
        // Option 1: Store ConsumerLogin ID as a claim in the JWT and retrieve it.
        // Option 2: Modify UserDetailsServiceImpl to return a custom UserDetails object containing the ID.
        // Option 3: Fetch ConsumerLogin from repository using the email (less efficient).
        logger.warn("Using placeholder logic to determine consumer ID. Implement properly!");
        // Example using Option 3 (inefficient):
        /*
        String email = ((User) authentication.getPrincipal()).getUsername();
        ConsumerLoginRepository consumerRepo = // Need to inject this repository
        Optional<ConsumerLogin> consumerOpt = consumerRepo.findByEmail(email);
        return consumerOpt.map(ConsumerLogin::getId).orElse(null);
        */
        // Placeholder - replace with real ID mapping
        if (authentication.getPrincipal() instanceof User) {
             // Simulating getting ID 1 for demo. Replace this!
             return 1L;
        }
        return null; 
    }

    /**
     * Step 2 logic: Send user's text to GPT, parse into { "action", "color", "carbonFootprint" }.
     */
    private NlpWishlistResponse callOpenAiToParse(String userText) {
        // Minimal version of Step 2 code
        String systemInstructions = """
            You are a helpful assistant that extracts the user's wishlist action from their text.
            The user might say:
            "Remove items which are blue and have a high carbon footprint."
            Return valid JSON with keys: "action", "color", and "carbonFootprint".
            Example:
            {"action": "remove", "color": "blue", "carbonFootprint": "high"}
            If any field is not specified, set it to an empty string.
        """;

        // Build the request payload
        JsonObject payload = new JsonObject();
        payload.addProperty("model", "gpt-3.5-turbo");
        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemInstructions);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userText);
        messages.add(userMessage);

        payload.add("messages", messages);

        // HTTP request to OpenAI
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);
        HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(OPENAI_ENDPOINT, HttpMethod.POST, entity, String.class);
        if (response.getBody() == null) {
            return new NlpWishlistResponse("OpenAI response empty", "", "", "");
        }

        JsonObject respJson = JsonParser.parseString(response.getBody()).getAsJsonObject();
        JsonArray choices = respJson.getAsJsonArray("choices");
        if (choices.size() == 0) {
            return new NlpWishlistResponse("No response from OpenAI", "", "", "");
        }

        String assistantContent = choices.get(0).getAsJsonObject()
                                         .getAsJsonObject("message")
                                         .get("content").getAsString();

        JsonObject filterData;
        try {
            filterData = JsonParser.parseString(assistantContent).getAsJsonObject();
        } catch (Exception e) {
            filterData = new JsonObject();
            filterData.addProperty("action", "");
            filterData.addProperty("color", "");
            filterData.addProperty("carbonFootprint", "");
        }

        String action = filterData.has("action") ? filterData.get("action").getAsString() : "";
        String color = filterData.has("color") ? filterData.get("color").getAsString() : "";
        String cfoot = filterData.has("carbonFootprint") ? filterData.get("carbonFootprint").getAsString() : "";

        return new NlpWishlistResponse("", action, color, cfoot);
    }

    /**
     * Step 3: If user says "remove items with color=blue, carbonFootprint=high",
     * actually remove them from the user's wishlist in Cassandra.
     */
    private int removeItemsFromWishlist(String userId, String color, String carbonFootprint) {
        // 1) GET the user from Cassandra 
        Consumer consumer = fetchConsumerFromCassandra(userId);
        if (consumer == null) {
            return 0; // or throw error
        }

        Set<String> wishlist = consumer.getWishlist();
        if (wishlist == null || wishlist.isEmpty()) {
            return 0;
        }

        // 2) Build a list of Tag objects from the user's wishlist
        //    We have to fetch each tag from Cassandra as well
        List<Tag> tagsInWishlist = new ArrayList<>();
        for (String tagId : wishlist) {
            Tag t = fetchTagFromCassandra(tagId);
            if (t != null) {
                tagsInWishlist.add(t);
            }
        }

        // 3) Evaluate which items match the user's filter 
        //    For color matching, we'll do a simple check if the color name is in any colourway
        //    For carbon footprint, define thresholds
        int removedCount = 0;
        Iterator<String> it = wishlist.iterator();
        while (it.hasNext()) {
            String tagId = it.next();
            Tag tagObj = tagsInWishlist.stream()
                    .filter(t -> tagId.equals(findTagId(t))) // we need some logic to know the ID
                    .findFirst()
                    .orElse(null);

            if (tagObj == null) continue;

            boolean matchesColor = doesColorMatch(tagObj, color);
            boolean matchesCarbon = doesCarbonFootprintMatch(tagObj, carbonFootprint);

            // If both match, remove from wishlist
            if (matchesColor && matchesCarbon) {
                it.remove();
                removedCount++;
            }
        }

        // 4) PUT updated Consumer back to Cassandra
        // (Re-save the updated wishlist)
        consumer = new Consumer(consumer.getName(), consumer.getTags(), wishlist);
        updateConsumerInCassandra(userId, consumer);

        return removedCount;
    }

    /**
     * Some naive color matching: 
     * - If color is empty, ignore color matching
     * - If Tag.colourways contain the color text, consider it a match
     */
    private boolean doesColorMatch(Tag tag, String filterColor) {
        if (filterColor.isEmpty()) return true;  // user didn't specify color => no color filter
        String[][] colourways = tag.getColourways(); // e.g. [ ["Blue", "#0000FF", "img.jpg"], ... ]
        for (String[] c : colourways) {
            if (c.length > 0) {
                // check if the color name (e.g. "Blue") matches filterColor ignoring case
                if (c[0].equalsIgnoreCase(filterColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Some naive carbon footprint matching:
     * - If carbonFootprint filter is empty => ignore
     * - We define numeric thresholds for "low", "medium", "high"
     */
    private boolean doesCarbonFootprintMatch(Tag tag, String filterCarbon) {
        if (filterCarbon.isEmpty()) return true; // user didn't specify => no filter

        double cfValue = tag.getCarbonFootprint(); // e.g. 75.2
        // define thresholds (sample logic)
        //   low < 30
        //   medium < 60
        //   high >= 60
        switch (filterCarbon.toLowerCase()) {
            case "low":
                return (cfValue < 30);
            case "medium":
                return (cfValue >= 30 && cfValue < 60);
            case "high":
                return (cfValue >= 60);
            default:
                return false;
        }
    }

    /**
     * Demo method to fetch a Consumer from Cassandra via REST
     * (We reuse or mimic the logic in ConsumerController, but do it inline for demo)
     */
    private Consumer fetchConsumerFromCassandra(String userId) {
        try {
            // e.g. GET /consumers/{id}
            String uri = String.format(
                "https://%s-%s.apps.astra.datastax.com/api/rest/v2/namespaces/%s/collections/consumer/%s",
                astraDbId, astraDbRegion, astraDbKeyspace, userId
            );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Cassandra-Token", astraDbToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                return null;
            }

            JsonObject root = JsonParser.parseString(res.getBody()).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data"); // consumer object
            // data should have "name", "tags", "wishlist", etc.

            String name = data.has("name") ? data.get("name").getAsString() : "Unknown";
            Set<String> tags = new HashSet<>();
            if (data.has("tags")) {
                for (JsonElement el : data.getAsJsonArray("tags")) {
                    tags.add(el.getAsString());
                }
            }
            Set<String> wishlist = new HashSet<>();
            if (data.has("wishlist")) {
                for (JsonElement el : data.getAsJsonArray("wishlist")) {
                    wishlist.add(el.getAsString());
                }
            }
            return new Consumer(name, tags, wishlist);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Save updated Consumer to Cassandra
     */
    private void updateConsumerInCassandra(String userId, Consumer consumer) {
        try {
            String uri = String.format(
                "https://%s-%s.apps.astra.datastax.com/api/rest/v2/namespaces/%s/collections/consumer/%s",
                astraDbId, astraDbRegion, astraDbKeyspace, userId
            );
            Gson gson = new Gson();
            String consumerJson = gson.toJson(consumer);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Cassandra-Token", astraDbToken);
            HttpEntity<String> entity = new HttpEntity<>(consumerJson, headers);

            restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            // handle error or logging
        }
    }

    /**
     * Fetch Tag from Cassandra
     */
    private Tag fetchTagFromCassandra(String tagId) {
        try {
            String uri = String.format(
                "https://%s-%s.apps.astra.datastax.com/api/rest/v2/namespaces/%s/collections/tag/%s",
                astraDbId, astraDbRegion, astraDbKeyspace, tagId
            );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Cassandra-Token", astraDbToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                return null;
            }
            // parse to Tag
            JsonObject root = JsonParser.parseString(res.getBody()).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");
            // We must convert it to Tag. For brevity, let's do a partial parse:

            String companyId = data.has("companyId") ? data.get("companyId").getAsString() : "";
            String name = data.has("name") ? data.get("name").getAsString() : "";
            String series = data.has("series") ? data.get("series").getAsString() : "";
            double unitPrice = data.has("unitPrice") ? data.get("unitPrice").getAsDouble() : 0;
            double salePrice = data.has("salePrice") ? data.get("salePrice").getAsDouble() : 0;
            // ... etc ...

            double carbonFootprint = data.has("carbonFootprint") ? data.get("carbonFootprint").getAsDouble() : 0;
            
            // parse colourways
            ArrayList<String[]> colourwaysList = new ArrayList<>();
            if (data.has("colourways")) {
                for (JsonElement arrEl : data.getAsJsonArray("colourways")) {
                    ArrayList<String> singleCW = new ArrayList<>();
                    for (JsonElement c : arrEl.getAsJsonArray()) {
                        singleCW.add(c.getAsString());
                    }
                    colourwaysList.add(singleCW.toArray(new String[0]));
                }
            }
            String[][] colourways = colourwaysList.toArray(new String[0][]);

            // For brevity, not showing all fields. In practice, parse the entire object or use Gson directly:
            // Tag tag = new Gson().fromJson(data, Tag.class);

            // Build minimal Tag with relevant fields
            Tag tag = new Tag(
                companyId, 
                name, 
                series, 
                unitPrice, 
                salePrice, 
                "",  // description
                colourways, 
                null,  // sizeChart
                null,  // media
                null,  // stories
                "", // materials
                "", // instructions
                null, // itemFeatures
                0,   // views
                0,   // saves
                carbonFootprint, 
                0,   // waterUsage
                0,   // recycledContentPercent
                "",  // wasteReductionPractices
                null,// qAndA
                null // userReviews
            );

            return tag;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * In your real code, you might store tag IDs in the Tag object, 
     * but here we have it as separate. Just an example placeholder.
     */
    private String findTagId(Tag t) {
        // In real code, you might store the ID in Tag, or pass it from fetchTagFromCassandra
        return t.getName() + "-" + t.getCompanyId(); 
        // Or store it in a custom field on Tag
    }
}
