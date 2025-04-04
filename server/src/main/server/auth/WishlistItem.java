package com.knowMoreQR.server.auth;

import javax.persistence.*;
import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "wishlist_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"consumer_id", "tag_id"})) // Prevent duplicate entries
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_id", nullable = false)
    private Long consumerId; // Foreign key to ConsumerLogin table

    @Column(name = "tag_id", nullable = false)
    private UUID tagId; // Foreign key to Tag table (Cassandra ID, stored in MySQL)

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    // Constructors
    public WishlistItem() {
        this.addedAt = Instant.now();
    }

    public WishlistItem(Long consumerId, UUID tagId) {
        this.consumerId = consumerId;
        this.tagId = tagId;
        this.addedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }
} 