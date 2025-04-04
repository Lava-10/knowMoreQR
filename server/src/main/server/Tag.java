package com.knowMoreQR.server;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

// Make Tag a Cassandra entity
@Table("tag") // Maps to the 'tag' table/collection
public class Tag {

    @PrimaryKey
    private UUID id; // Add a UUID primary key

    private String companyId;
    private String name;
    private String series;
    private double unitPrice;
    private double salePrice;
    private String description;
    private String[][] colourways;
    private float[][] sizeChart;
    private String[] media;
    private String[][] stories;
    private String materials;
    private String instructions;
    private String[] itemFeatures;
    private int views; // number of views by consumers
    private int saves; // amount wishlisted by consumers
    private double carbonFootprint;         // e.g. kg CO2
    private double waterUsage;              // e.g. liters used
    private double recycledContentPercent;  // e.g. 30 => 30%
    private String wasteReductionPractices; // short text describing methods
    private String[][] qAndA;
    private String[] userReviews;

    // Default constructor for Spring Data Cassandra
    public Tag() {
        this.id = UUID.randomUUID(); // Generate ID by default? Or set during save?
    }

    // Constructor for creating new tags (might be used by application logic)
    // Removed final keywords from fields
    public Tag(String companyId, String name, String series, double unitPrice, double salePrice, String description,
               String[][] colourways, float[][] sizeChart, String[] media, String[][] stories, String materials,
               String instructions, String[] itemFeatures, int views, int saves,
               double carbonFootprint, double waterUsage, double recycledContentPercent, String wasteReductionPractices,
               String[][] qAndA, String[] userReviews) {
        this(); // Call default constructor to set ID
        this.companyId = companyId;
        this.name = name;
        this.series = series;
        // Rounding can be done here or handled by getter/setter/database type
        this.unitPrice = unitPrice; //Math.round(unitPrice * 100) / (double) 100;
        this.salePrice = salePrice; //Math.round(salePrice * 100) / (double) 100;
        this.description = description;
        this.colourways = colourways;
        this.sizeChart = sizeChart;
        this.media = media;
        this.stories = stories;
        this.materials = materials;
        this.instructions = instructions;
        this.itemFeatures = itemFeatures;
        this.views = views;
        this.saves = saves;
        this.carbonFootprint = carbonFootprint;
        this.waterUsage = waterUsage;
        this.recycledContentPercent = recycledContentPercent;
        this.wasteReductionPractices = wasteReductionPractices;
        this.qAndA = qAndA;
        this.userReviews = userReviews;
    }

    // --- Getters and Setters --- 
    // (Adding setters allows Spring Data to populate the object)

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[][] getColourways() {
        return colourways;
    }

    public void setColourways(String[][] colourways) {
        this.colourways = colourways;
    }

    public float[][] getSizeChart() {
        return sizeChart;
    }

    public void setSizeChart(float[][] sizeChart) {
        this.sizeChart = sizeChart;
    }

    public String[] getMedia() {
        return media;
    }

    public void setMedia(String[] media) {
        this.media = media;
    }

    public String[][] getStories() {
        return stories;
    }

    public void setStories(String[][] stories) {
        this.stories = stories;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String[] getItemFeatures() {
        return itemFeatures;
    }

    public void setItemFeatures(String[] itemFeatures) {
        this.itemFeatures = itemFeatures;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public double getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(double carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public double getWaterUsage() {
        return waterUsage;
    }

    public void setWaterUsage(double waterUsage) {
        this.waterUsage = waterUsage;
    }

    public double getRecycledContentPercent() {
        return recycledContentPercent;
    }

    public void setRecycledContentPercent(double recycledContentPercent) {
        this.recycledContentPercent = recycledContentPercent;
    }

    public String getWasteReductionPractices() {
        return wasteReductionPractices;
    }

    public void setWasteReductionPractices(String wasteReductionPractices) {
        this.wasteReductionPractices = wasteReductionPractices;
    }

    public String[][] getQAndA() {
        return qAndA;
    }

    public void setQAndA(String[][] qAndA) {
        this.qAndA = qAndA;
    }

    public String[] getUserReviews() {
        return userReviews;
    }

    public void setUserReviews(String[] userReviews) {
        this.userReviews = userReviews;
    }

    // Removing old getters that had different names
    /*
    public String[] getSustainability() {
        return this.media;
    }

    public String[][] getLabour() {
        return this.stories;
    }
    */
}
