package com.knowMoreQR.server;

public class Tag {

   
    private final String companyId;
    private final String name;
    private final String series;
    private final double unitPrice;
    private final double salePrice;
    private final String description;
    private final String[][] colourways;
    private final float[][] sizeChart;
    private final String[] media;
    private final String[][] stories;
    private final String materials;
    private final String instructions;
    private final String[] itemFeatures;
    private final int views; // number of views by consumers
    private final int saves; // amount wishlisted by consumers
     private final double carbonFootprint;         // e.g. kg CO2
    private final double waterUsage;              // e.g. liters used
    private final double recycledContentPercent;  // e.g. 30 => 30%
    private final String wasteReductionPractices; // short text describing methods

    // 2) Customer Engagement
    // Q&A: each sub-array might be ["question", "answer"]
    private final String[][] qAndA;  
    // userReviews: each entry is a single user comment or testimonial
    private final String[] userReviews;


     public Tag(String companyId, String name, String series, double unitPrice, double salePrice, String description,
               String[][] colourways, float[][] sizeChart, String[] media, String[][] stories, String materials,
               String instructions, String[] itemFeatures, int views, int saves,
               double carbonFootprint, double waterUsage, double recycledContentPercent, String wasteReductionPractices,
               String[][] qAndA, String[] userReviews) {

        this.companyId = companyId;
        this.name = name;
        this.series = series;
        this.unitPrice = Math.round(unitPrice * 100) / (double) 100;
        this.salePrice = Math.round(salePrice * 100) / (double) 100;
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

    public String getCompanyId() {
        return this.companyId;
    }

    public String getName() {
        return this.name;
    }

    public String getSeries() {
        return this.series;
    }

    public double getUnitPrice() {
        return this.unitPrice;
    }

    public double getSalePrice() {
        return this.salePrice;
    }

    public int getViews() {
        return this.views;
    }

    public int getSaves() {
        return this.saves;
    }

    public String getDescription() {
        return this.description;
    }

    public float[][] getSizeChart() {
        return this.sizeChart;
    }

    public String[][] getColourways() {
        return this.colourways;
    }

    public String getMaterials() {
        return this.materials;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public String[] getSustainability() {
        return this.media;
    }

    public String[][] getLabour() {
        return this.stories;
    }

    public String[] getItemFeatures() {
        return this.itemFeatures;
    }


    public double getCarbonFootprint() { return carbonFootprint; }
    public double getWaterUsage() { return waterUsage; }
    public double getRecycledContentPercent() { return recycledContentPercent; }
    public String getWasteReductionPractices() { return wasteReductionPractices; }
    public String[][] getQAndA() { return qAndA; }
    public String[] getUserReviews() { return userReviews; }
}
