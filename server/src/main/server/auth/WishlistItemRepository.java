package com.knowMoreQR.server.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    // Find all items for a specific consumer
    List<WishlistItem> findByConsumerIdOrderByAddedAtDesc(Long consumerId);

    // Find a specific item for a consumer by tagId
    Optional<WishlistItem> findByConsumerIdAndTagId(Long consumerId, UUID tagId);

    // Check if an item exists
    boolean existsByConsumerIdAndTagId(Long consumerId, UUID tagId);

    // Delete specific item(s) - potentially multiple if criteria match
    void deleteByConsumerIdAndTagId(Long consumerId, UUID tagId);

    // Delete all items for a consumer
    void deleteByConsumerId(Long consumerId);

    // Could add methods to find items based on criteria from the Tag (requires joining or separate queries)
    // e.g., findByConsumerIdAndTag_Color(...) - This is complex as Tag is in Cassandra.
} 