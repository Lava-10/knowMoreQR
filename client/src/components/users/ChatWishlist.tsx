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
  const [wishlist, setWishlist] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  // Hard-coded for demo. In a real app, retrieve from auth context or route param
  const userId = "6363c590-2b22-4cbe-af9f-862b5b2cc2e0";

  // Initialize with welcome message
  useEffect(() => {
    setConversation(["System: Hello! How can I help with your wishlist today?"]);
    fetchWishlist();
  }, []);

  // Define a function to fetch the wishlist from your backend
  const fetchWishlist = async () => {
    try {
      setLoading(true);
      setError("");
      
      // API call to get the consumer's wishlist
      const res = await axios.get(`/consumers/${userId}`);
      const wish = res.data.data.wishlist;
      setWishlist(Array.isArray(wish) ? wish : []);
    } catch (err: any) {
      console.error("Error fetching wishlist:", err);
      setError("Could not load your wishlist. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  // Handle the user's chat command
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!message.trim()) return;

    // Append user's message to conversation
    setConversation(prev => [...prev, `You: ${message}`]);
    
    // Save the message and clear input
    const userMessage = message;
    setMessage("");
    
    try {
      setLoading(true);
      
      // Show typing indicator
      setConversation(prev => [...prev, "System: typing..."]);
      
      // POST user's request to /api/nlp-wishlist
      const res = await axios.post("/api/nlp-wishlist", {
        userId,
        text: userMessage
      });

      // Remove typing indicator and show actual response
      setConversation(prev => {
        const updated = [...prev];
        updated[updated.length - 1] = `System: ${res.data.message}`;
        return updated;
      });

      // Re-fetch the updated wishlist to see changes
      await fetchWishlist();
    } catch (err: any) {
      console.error("Error processing command:", err);
      
      // Remove typing indicator and show error
      setConversation(prev => {
        const updated = [...prev];
        updated[updated.length - 1] = "System: Sorry, I couldn't process your request. Please try again.";
        return updated;
      });
      
      setError(err.response?.data?.message || "Failed to process your command");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="columns is-centered">
        <div className="column is-8">
          <div className="box">
            <h1 className="title is-4 has-text-centered">AI Wishlist Assistant</h1>
            <p className="subtitle is-6 has-text-centered">
              Use natural language to manage your sustainable product wishlist
            </p>
            
            {/* Conversation area */}
            <div 
              className="box" 
              style={{ 
                minHeight: "300px", 
                maxHeight: "400px", 
                overflowY: "auto", 
                marginBottom: "1rem",
                background: "#f8f9fa"
              }}
            >
              {conversation.map((line, idx) => (
                <div 
                  key={idx} 
                  className={`chat-message ${line.startsWith("You:") ? "has-text-right" : ""}`} 
                  style={{ 
                    marginBottom: "0.8rem",
                    padding: "0.5rem",
                    borderRadius: "8px",
                    background: line.startsWith("You:") ? "#e3f2fd" : "#ffffff"
                  }}
                >
                  {line}
                </div>
              ))}
            </div>
            
            {/* Error message if exists */}
            {error && (
              <div className="notification is-danger is-light">
                <button className="delete" onClick={() => setError("")}></button>
                {error}
              </div>
            )}

            {/* Input form */}
            <form onSubmit={handleSubmit}>
              <div className="field has-addons">
                <div className="control is-expanded">
                  <input
                    className="input"
                    type="text"
                    value={message}
                    placeholder="Type a command, e.g. 'Remove items with high carbon footprint'"
                    onChange={(e) => setMessage(e.target.value)}
                    disabled={loading}
                  />
                </div>
                <div className="control">
                  <button 
                    type="submit" 
                    className={`button is-primary ${loading ? "is-loading" : ""}`}
                    disabled={loading || !message.trim()}
                  >
                    Send
                  </button>
                </div>
              </div>
            </form>

            {/* Wishlist display */}
            <div className="box mt-4">
              <h2 className="title is-5">Your Wishlist</h2>
              {loading && <p className="has-text-centered">Loading...</p>}
              
              {!loading && wishlist.length === 0 ? (
                <p className="has-text-centered">No items in wishlist. Try adding something!</p>
              ) : (
                <div className="content">
                  <ul>
                    {wishlist.map((tagId, index) => (
                      <li key={index} className="mb-2">
                        <strong>Tag ID:</strong> {tagId}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
              
              <div className="has-text-centered mt-4">
                <p className="is-size-7">
                  <strong>Try these commands:</strong> "Add all sustainable items", 
                  "Remove items with high carbon footprint", "Show me items from ethical brands"
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatWishlist;
