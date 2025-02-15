package com.knowMoreQR.server.auth;

import com.knowMoreQR.server.Tag;
import java.util.List;

public class NlpWishlistResponse {

    private boolean success;
    private String message;
    private String aiAnalysis; // Raw analysis from AI
    private List<Tag> wishlistItems; // Actual wishlist items (Tag details)

    // ... existing fields like action, color, etc. can be removed if only using aiAnalysis ...
    private String action;
    private String color;
    private String carbonFootprint;
    private String targetItem;

    public NlpWishlistResponse() {
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(String aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public List<Tag> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<Tag> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    // --- Keep or Remove old getters/setters based on final parsing strategy ---
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCarbonFootprint() { return carbonFootprint; }
    public void setCarbonFootprint(String carbonFootprint) { this.carbonFootprint = carbonFootprint; }
    public String getTargetItem() { return targetItem; }
    public void setTargetItem(String targetItem) { this.targetItem = targetItem; }

} 