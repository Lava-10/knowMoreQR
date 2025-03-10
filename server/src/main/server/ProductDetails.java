package com.knowMoreQR.server;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("product_details") // ensure you have created this table in Cassandra
public class ProductDetails {

    @PrimaryKey
    private UUID id;

    private String name;
    private BigDecimal price;
    private String category;

    // We'll store the entire OCR text for reference
    private String ocrText;

    // Additional field to hold everything as a "description"
    private String description;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOcrText() { return ocrText; }
    public void setOcrText(String ocrText) { this.ocrText = ocrText; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
