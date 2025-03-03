// NlpWishlistResponse.java
package com.knowMoreQR.server;

public class NlpWishlistResponse {
    private String message;
    private String action; // e.g. "remove", "add", etc.
    private String color;  // e.g. "blue", "red", ...
    private String carbonFootprint; // e.g. "high", "medium", "low"
    // add any more fields as needed

    public NlpWishlistResponse() {}

    public NlpWishlistResponse(String message, String action, String color, String carbonFootprint) {
        this.message = message;
        this.action = action;
        this.color = color;
        this.carbonFootprint = carbonFootprint;
    }

    public String getMessage() {
        return message;
    }
    public String getAction() {
        return action;
    }
    public String getColor() {
        return color;
    }
    public String getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public void setCarbonFootprint(String carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }
}
/*
we can adjust these fields however you like, especially if you anticipate more filters (price range, size, etc.).

*/