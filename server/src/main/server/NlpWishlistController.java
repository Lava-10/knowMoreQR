package com.knowMoreQR.server;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.google.gson.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NlpWishlistController {

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

    @PostMapping("/nlp-wishlist")
    public ResponseEntity<NlpWishlistResponse> processWishlist(@RequestBody NlpWishlistRequest request) {
        // 1) Extract user input
        String userId = request.getUserId();
        String userText = request.getText();

        // 2) Call OpenAI to parse the user’s text into a structured filter
        NlpWishlistResponse parsedFilters = callOpenAiToParse(userText);
        String action = parsedFilters.getAction();               // e.g. "remove", "add"
        String color = parsedFilters.getColor();                 // e.g. "blue", "red"
        String carbonFootprint = parsedFilters.getCarbonFootprint(); // e.g. "high", "medium", "low"

        // If we didn’t parse anything meaningful, just return
        if (action.isEmpty() && color.isEmpty() && carbonFootprint.isEmpty()) {
            parsedFilters.setMessage("Could not parse your request. Please try again.");
            return ResponseEntity.ok(parsedFilters);
        }

        // 3) Based on the action, modify the user’s wishlist
        // We'll focus on "remove" as the example, but you could extend for "add", etc.
        String resultMsg;
        if (action.equalsIgnoreCase("remove")) {
            // 3a) Remove matching items from wishlist
            int removedCount = removeItemsFromWishlist(userId, color, carbonFootprint);
            resultMsg = "Removed " + removedCount + " items from your wishlist.";
        } 
        else if (action.equalsIgnoreCase("add")) {
            // Example placeholder: you'd parse from user text which product they want to add
            // For brevity, let's say we always do nothing:
            resultMsg = "Add action recognized, but not yet implemented.";
        } 
        else {
            // default case
            resultMsg = "Action \"" + action + "\" not recognized or not implemented yet.";
        }

        // 4) Return final message to front-end
        parsedFilters.setMessage(resultMsg);
        return ResponseEntity.ok(parsedFilters);
    }

    /**
     * Step 2 logic: Send user’s text to GPT, parse into { "action", "color", "carbonFootprint" }.
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

        // 2) Build a list of Tag objects from the user’s wishlist
        //    We have to fetch each tag from Cassandra as well
        List<Tag> tagsInWishlist = new ArrayList<>();
        for (String tagId : wishlist) {
            Tag t = fetchTagFromCassandra(tagId);
            if (t != null) {
                tagsInWishlist.add(t);
            }
        }

        // 3) Evaluate which items match the user's filter 
        //    For color matching, we’ll do a simple check if the color name is in any colourway
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

    @PostMapping("/nlp-wishlist")
public ResponseEntity<NlpWishlistResponse> processWishlist(@RequestBody NlpWishlistRequest request) {
    String userId = request.getUserId();
    String userText = request.getText();

    // 1) Ask OpenAI for {action, color, carbonFootprint, targetItem}
    NlpWishlistResponse parsedFilters = callOpenAiToParse(userText);
    String action = parsedFilters.getAction();  
    String color = parsedFilters.getColor();
    String carbonFootprint = parsedFilters.getCarbonFootprint();
    String targetItem = parsedFilters.getTargetItem();

    // If we didn’t parse anything meaningful, return
    if (action.isEmpty() && color.isEmpty() && carbonFootprint.isEmpty() && targetItem.isEmpty()) {
        parsedFilters.setMessage("Could not parse your request. Please try again.");
        return ResponseEntity.ok(parsedFilters);
    }

    // 2) Decide which action to take
    String resultMsg;
    if (action.equalsIgnoreCase("remove")) {
        int removedCount = removeItemsFromWishlist(userId, color, carbonFootprint);
        resultMsg = "Removed " + removedCount + " items from your wishlist.";
    } 
    else if (action.equalsIgnoreCase("add")) {
        int addedCount = addItemsToWishlist(userId, targetItem);
        if (addedCount > 0) {
            resultMsg = "Added " + addedCount + " items to your wishlist.";
        } else {
            resultMsg = "No items found matching '" + targetItem + "' to add.";
        }
    } 
    else {
        resultMsg = "Action \"" + action + "\" not recognized or not implemented yet.";
    }

    parsedFilters.setMessage(resultMsg);
    return ResponseEntity.ok(parsedFilters);
}

    /**
 * Example: user says "Add that green sweater to my wishlist."
 * targetItem might be "green sweater".
 * We'll do a naive search in Cassandra for any tags with name/series containing "green sweater" (case-insensitive).
 * Then we add them to the user's wishlist.
 */
private int addItemsToWishlist(String userId, String targetItem) {
    if (targetItem == null || targetItem.trim().isEmpty()) {
        return 0; // user didn't specify item to add
    }

    // 1) fetch the user
    Consumer consumer = fetchConsumerFromCassandra(userId);
    if (consumer == null) return 0;

    Set<String> wishlist = consumer.getWishlist();
    if (wishlist == null) {
        wishlist = new HashSet<>();
    }

    // 2) search tags by name/series
    // We can do a naive approach: fetch all tags from /tags, filter locally
    // or build a query with where={ "name": {"$contains": targetItem} } if available
    // For the example, we fetch everything and do a local match:
    List<String> matchingTagIds = findTagIdsByName(targetItem);

    int addedCount = 0;
    for (String tagId : matchingTagIds) {
        // only add if not already in wishlist
        if (!wishlist.contains(tagId)) {
            wishlist.add(tagId);
            addedCount++;
        }
    }

    // 3) update the consumer
    Consumer updated = new Consumer(consumer.getName(), consumer.getTags(), wishlist);
    updateConsumerInCassandra(userId, updated);

    return addedCount;
}

/**
 * For demo, we do a naive approach: GET /tags, loop over them, see if name/series contains `targetItem`.
 * Then return a list of their IDs.
 */
private List<String> findTagIdsByName(String partialText) {
    partialText = partialText.toLowerCase();
    List<String> results = new ArrayList<>();

    try {
        // GET all tags from Cassandra: /tags (like in TagController with page-size=20)
        String uri = String.format(
            "https://%s-%s.apps.astra.datastax.com/api/rest/v2/namespaces/%s/collections/tag?page-size=100",
            astraDbId, astraDbRegion, astraDbKeyspace
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Cassandra-Token", astraDbToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
            return results;
        }

        JsonObject root = JsonParser.parseString(res.getBody()).getAsJsonObject();
        JsonObject data = root.getAsJsonObject("data");

        // data is a map of { tagId: { "companyId": "...", "name": "...", ... }, ... }
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            String tagId = entry.getKey();
            JsonObject tagObj = entry.getValue().getAsJsonObject().getAsJsonObject();

            String name = tagObj.has("name") ? tagObj.get("name").getAsString().toLowerCase() : "";
            String series = tagObj.has("series") ? tagObj.get("series").getAsString().toLowerCase() : "";

            // if partialText is found in name or series
            if (name.contains(partialText) || series.contains(partialText)) {
                results.add(tagId);
            }
        }
    } catch (Exception e) {
        // handle or log
    }

    return results;
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
