package com.knowMoreQR.server.service;

import com.knowMoreQR.server.Tag;
import com.knowMoreQR.server.TagRepository;
import com.knowMoreQR.server.auth.WishlistItem;
import com.knowMoreQR.server.auth.WishlistItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private TagRepository tagRepository; // To fetch Tag details

    // --- Core Wishlist Actions --- 

    @Transactional // Ensure atomicity if needed
    public WishlistItem addItem(Long consumerId, UUID tagId) {
        // Check if tag exists in Cassandra
        if (!tagRepository.existsById(tagId)) {
            logger.warn("Attempted to add non-existent tag {} to wishlist for consumer {}", tagId, consumerId);
            throw new IllegalArgumentException("Tag with ID " + tagId + " not found.");
        }
        
        // Check if item already exists in wishlist
        if (wishlistItemRepository.existsByConsumerIdAndTagId(consumerId, tagId)) {
            logger.info("Tag {} already in wishlist for consumer {}", tagId, consumerId);
            // Return existing item or handle as needed
            return wishlistItemRepository.findByConsumerIdAndTagId(consumerId, tagId).orElse(null);
        }
        
        WishlistItem newItem = new WishlistItem(consumerId, tagId);
        WishlistItem savedItem = wishlistItemRepository.save(newItem);
        logger.info("Added tag {} to wishlist for consumer {}", tagId, consumerId);
        return savedItem;
    }

    @Transactional
    public boolean removeItem(Long consumerId, UUID tagId) {
        Optional<WishlistItem> itemOpt = wishlistItemRepository.findByConsumerIdAndTagId(consumerId, tagId);
        if (itemOpt.isPresent()) {
            wishlistItemRepository.delete(itemOpt.get());
            logger.info("Removed tag {} from wishlist for consumer {}", tagId, consumerId);
            return true;
        } else {
            logger.warn("Attempted to remove non-existent tag {} from wishlist for consumer {}", tagId, consumerId);
            return false;
        }
    }

    @Transactional
    public void clearWishlist(Long consumerId) {
        wishlistItemRepository.deleteByConsumerId(consumerId);
        logger.info("Cleared wishlist for consumer {}", consumerId);
    }

    // --- Wishlist Retrieval --- 

    public List<WishlistItem> getWishlistItems(Long consumerId) {
        return wishlistItemRepository.findByConsumerIdOrderByAddedAtDesc(consumerId);
    }

    // Method to get the full Tag details for items in the wishlist
    public List<Tag> getWishlistTags(Long consumerId) {
        List<WishlistItem> items = getWishlistItems(consumerId);
        List<UUID> tagIds = items.stream()
                                 .map(WishlistItem::getTagId)
                                 .collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return List.of(); // Return empty list if no tag IDs
        }
        
        // Fetch all corresponding tags from Cassandra
        return tagRepository.findAllById(tagIds);
    }

    // --- Methods for NLP integration (Example Placeholders) ---

    // Example: Find tags matching some criteria (e.g., name containing text)
    // This would ideally use more efficient Cassandra queries if possible
    public List<Tag> findTagsByName(String nameQuery) {
        // Naive implementation: fetch all and filter (inefficient for large datasets!)
        // Consider adding specific query methods to TagRepository if feasible
        logger.warn("findTagsByName performing inefficient scan. Consider optimizing Cassandra query.");
        String lowerCaseQuery = nameQuery.toLowerCase();
        return tagRepository.findAll().stream()
                .filter(tag -> (tag.getName() != null && tag.getName().toLowerCase().contains(lowerCaseQuery)) || 
                               (tag.getSeries() != null && tag.getSeries().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }
    
    // Example: Find tags matching sustainability criteria (more complex)
    // public List<Tag> findTagsBySustainability(String criteria) { ... }
    
} 