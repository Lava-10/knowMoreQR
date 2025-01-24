import React, { useState, useEffect, FormEvent } from 'react';
import axios from 'axios';
import { useAuth } from '../../context/AuthContext';
import { Tag } from '../../types'; // Import Tag type

/**
 * ChatWishlist component:
 * - Displays user's current wishlist
 * - Provides a text input for user commands
 * - Calls a backend endpoint (POST /api/nlp-wishlist) for AI-based instructions
 * - Re-fetches the updated wishlist from the backend afterward
 */

interface NlpResponse {
    success: boolean;
    message: string;
    aiAnalysis?: string;
    wishlistItems?: Tag[];
}

const ChatWishlist: React.FC = () => {
    const { isAuthenticated } = useAuth();
    const [command, setCommand] = useState<string>('');
    const [wishlist, setWishlist] = useState<Tag[]>([]);
    const [conversation, setConversation] = useState<Array<{ type: 'user' | 'system', text: string }>>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    // Hard-coded for demo. In a real app, retrieve from auth context or route param
    const userId = "6363c590-2b22-4cbe-af9f-862b5b2cc2e0";

    // Fetch initial wishlist on component mount
    useEffect(() => {
        if (isAuthenticated) {
            fetchWishlist();
        }
        // Add welcome message to conversation only once
        setConversation([{ type: 'system', text: "Hello! How can I help with your wishlist today?" }]);
    }, [isAuthenticated]); // Run only when auth status changes

    const fetchWishlist = async () => {
        setIsLoading(true);
        setError('');
        try {
            const response = await axios.post<NlpResponse>('/api/nlp/wishlist', { command: 'view my wishlist' });
            if (response.data.success && response.data.wishlistItems) {
                setWishlist(response.data.wishlistItems);
            } else {
                setError(response.data.message || 'Failed to fetch initial wishlist.');
                setWishlist([]); // Clear wishlist on error
            }
        } catch (err) {
            console.error("Error fetching wishlist:", err);
            setError('Failed to fetch wishlist. Please try again later.');
            setWishlist([]); // Clear wishlist on error
        } finally {
            setIsLoading(false);
        }
    };

    const handleCommandSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (!command.trim() || isLoading) return;

        const userMessage = { type: 'user' as const, text: command };
        setConversation((prev: Array<{ type: 'user' | 'system', text: string }>) => [...prev, userMessage]);
        const currentCommand = command; // Store command before clearing
        setCommand(''); 
        setIsLoading(true);
        setError('');

        try {
            const response = await axios.post<NlpResponse>('/api/nlp/wishlist', { command: currentCommand }); // Use stored command
            const systemMessage = { 
                type: 'system' as const, 
                // Use only the main message for conversation, AI analysis can be logged or shown differently if desired
                text: response.data.message 
            };
            setConversation((prev: Array<{ type: 'user' | 'system', text: string }>) => [...prev, systemMessage]);

            if (response.data.success) {
                if (response.data.wishlistItems !== undefined) {
                    setWishlist(response.data.wishlistItems || []);
                }
            } else {
                setError(response.data.message || 'Command failed.');
            }
        } catch (err: any) {
            console.error("Error processing command:", err);
            const errorMsg = err.response?.data?.message || 'An error occurred processing your request.';
            setError(`Error: ${errorMsg}`);
            setConversation((prev: Array<{ type: 'user' | 'system', text: string }>) => [...prev, { type: 'system', text: `Error: ${errorMsg}` }]);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container mt-5 pt-5">
            <h1 className="title">Chat Wishlist</h1>
            <p className="subtitle">Manage your wishlist using natural language (e.g., "add blue shirt", "show my list", "remove pants").</p>

            {/* Wishlist Display */}
            <div className="box mb-4">
                <h2 className="subtitle is-4">Current Wishlist ({wishlist.length})</h2>
                {isLoading && wishlist.length === 0 && <p>Loading wishlist...</p>}
                {!isLoading && error && wishlist.length === 0 && <p className="has-text-danger">Error loading wishlist: {error}</p>}
                {!isLoading && !error && wishlist.length === 0 && <p>Your wishlist is empty. Try adding an item!</p>}
                {wishlist.length > 0 && (
                    <div className="list is-hoverable">
                        {wishlist.map(item => (
                            <div className="list-item" key={item.id}>
                                <div className="list-item-content">
                                    <div className="list-item-title">{item.name || 'Unknown Item'}</div>
                                    <div className="list-item-description">
                                        Series: {item.series || 'N/A'} | Price: ${item.salePrice?.toFixed(2) ?? item.unitPrice?.toFixed(2) ?? 'N/A'}
                                        {/* Add button to remove directly? Or view details? */}
                                        {/* <button className="button is-small is-danger is-light ml-3" onClick={() => handleCommandSubmit(/* create remove event * /)}>Remove</button> */}
                                    </div>
                                </div>
                                {/* Optionally display first image */}
                                {item.media && item.media.length > 0 && (
                                    <div className="list-item-image is-hidden-mobile">
                                        <figure className="image is-64x64">
                                            <img src={item.media[0]} alt={item.name} style={{ objectFit: 'cover' }} />
                                        </figure>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Conversation Display */}
            <div className="box mb-4" style={{ height: '300px', overflowY: 'scroll' }}>
                {conversation.map((msg, index) => (
                    <div key={index} className={`message-container ${msg.type === 'user' ? 'is-user' : 'is-system'}`}>
                       <div className={`message-bubble ${msg.type === 'user' ? 'is-info' : 'is-light'}`}>
                         {msg.text}
                       </div>
                    </div>
                ))}
                 {isLoading && <p className="has-text-grey has-text-centered is-italic">System processing...</p>}
            </div>

            {/* Command Input */}
            <form onSubmit={handleCommandSubmit}>
                <label htmlFor="wishlistCommand" className="label">Enter Command:</label>
                <div className="field has-addons">
                    <div className="control is-expanded">
                        <input 
                            id="wishlistCommand"
                            className="input" 
                            type="text" 
                            placeholder="e.g., add red dress, remove blue sweater, clear list" 
                            value={command}
                            onChange={e => setCommand(e.target.value)}
                            disabled={isLoading}
                            aria-label="Wishlist Command Input"
                        />
                    </div>
                    <div className="control">
                        <button type="submit" className={`button is-info ${isLoading ? 'is-loading' : ''}`} disabled={isLoading || !command.trim()}>
                            Send
                        </button>
                    </div>
                </div>
                {/* Display specific error from API call if exists, otherwise clear */}
                {error && !isLoading && <p className="help is-danger mt-2">{error}</p>}
            </form>

            {/* Basic CSS for Chat Bubbles (add to a .css/.scss file if preferred) */}
            <style>{`
                .message-container {
                    display: flex;
                    margin-bottom: 0.5rem;
                }
                .message-container.is-user {
                    justify-content: flex-end;
                }
                 .message-container.is-system {
                    justify-content: flex-start;
                }
                .message-bubble {
                    padding: 0.5rem 1rem;
                    border-radius: 1rem;
                    max-width: 70%;
                    word-wrap: break-word;
                }
                .message-bubble.is-info {
                    background-color: #209cee; // Bulma info color
                    color: white;
                    border-bottom-right-radius: 0.25rem;
                }
                 .message-bubble.is-light {
                    background-color: #f5f5f5; // Bulma light color
                    color: #363636;
                    border-bottom-left-radius: 0.25rem;
                }
            `}</style>

        </div>
    );
};

export default ChatWishlist;
