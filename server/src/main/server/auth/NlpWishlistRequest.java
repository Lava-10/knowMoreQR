// NlpWishlistRequest.java


/*

This class represents the JSON object we expect from the front-end:

{
  "userId": "abc123",
  "text": "Remove items from my wishlist that are blue and have high carbon footprint"
}


*/
package com.knowMoreQR.server;

public class NlpWishlistRequest {
    private String userId;
    private String text;

    public NlpWishlistRequest() {}

    public NlpWishlistRequest(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }
}
