import React, { useState, useEffect } from 'react';
import axios from 'axios';

/**
 * ChatWishlist component:
 * - Displays user's current wishlist
 * - Provides a text input for user commands
 * - Calls a backend endpoint (POST /api/nlp-wishlist) for AI-based instructions
 * - Re-fetches the updated wishlist from the backend afterward
 */

const ChatWishlist: React.FC = () => {
  const [message, setMessage] = useState("");
  const [conversation, setConversation] = useState<string[]>([]);

  // NEW: store the user’s wishlist items in local state
  // We'll assume each item is just an ID or a small object {id: string, name: string} if we want more details
  const [wishlist, setWishlist] = useState<string[]>([]);

  // Hard-coded for demo. In a real app, retrieve from auth context or route param
  const userId = "6363c590-2b22-4cbe-af9f-862b5b2cc2e0";

  // 1) On component mount, fetch the user’s wishlist
  useEffect(() => {
    fetchWishlist();
  }, []);

  // 2) Define a function to fetch the wishlist from your backend
  const fetchWishlist = async () => {
    try {
      // We'll assume you have an endpoint GET /consumers/:id
      // that returns { data: { name: string, tags: string[], wishlist: string[] } }
      const res = await axios.get(`/consumers/${userId}`);
      // The response might look like:
      // {
      //   "data": {
      //       "name": "John",
      //       "tags": [...],
      //       "wishlist": ["tagId1", "tagId2", ...]
      //   }
      // }
      const wish = res.data.data.wishlist;
      setWishlist(wish);
    } catch (err) {
      console.log("Error fetching wishlist:", err);
    }
  };

  // 3) Handle the user’s chat command
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!message.trim()) return;

    // Append user’s message to conversation
    setConversation(prev => [...prev, `You: ${message}`]);

    try {
      // POST user’s request to /api/nlp-wishlist
      const res = await axios.post("/api/nlp-wishlist", {
        userId,
        text: message
      });
      // E.g. returns { message: "Removed 2 items from your wishlist.", action: "remove", color: "blue", ... }

      // Show system response in chat
      setConversation(prev => [...prev, `System: ${res.data.message}`]);

      // NEW: Re-fetch the updated wishlist to see changes
      await fetchWishlist();

    } catch (err: any) {
      console.log(err);
      setConversation(prev => [...prev, "System: Error occurred."]);
    }

    setMessage("");
  };

  return (
    <div style={{ maxWidth: "600px", margin: "0 auto" }}>
      <h1 className="title is-size-4 has-text-centered mt-5">Wishlist Chat</h1>

      {/* Conversation area */}
      <div className="box" style={{ minHeight: "250px", overflowY: "auto" }}>
        {conversation.map((line, idx) => (
          <div key={idx} style={{ marginBottom: "0.5rem" }}>
            {line}
          </div>
        ))}
      </div>

      {/* Input form */}
      <form onSubmit={handleSubmit} className="mt-3">
        <div className="field has-addons">
          <div className="control is-expanded">
            <input
              className="input"
              type="text"
              value={message}
              placeholder="Type a command, e.g. 'Remove items with high carbon footprint'"
              onChange={(e) => setMessage(e.target.value)}
            />
          </div>
          <div className="control">
            <button type="submit" className="button is-primary">
              Send
            </button>
          </div>
        </div>
      </form>

      {/* NEW: Display the wishlist below */}
      <div className="box mt-5">
        <h2 className="title is-size-5">Your Wishlist</h2>
        {wishlist.length === 0 ? (
          <p>No items in wishlist.</p>
        ) : (
          <ul>
            {wishlist.map((tagId, index) => (
              <li key={index}>
                <strong>Tag ID:</strong> {tagId}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default ChatWishlist;
