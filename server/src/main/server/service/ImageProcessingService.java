package com.knowMoreQR.server.service;

import com.knowMoreQR.server.ProductDetails;
import com.knowMoreQR.server.ProductDetailsRepository;
import net.sourceforge.tess4j.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingService.class);
    
    private final ProductDetailsRepository productDetailsRepository;

    public ImageProcessingService(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    public ProductDetails processImage(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        
        File tempFile = File.createTempFile("ocr-upload-", ".jpg");
        multipartFile.transferTo(tempFile);
        
        try {
            String extractedText = doOCR(tempFile);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                logger.warn("OCR extracted no text from the image");
            }

            ProductDetails details = parseTextToProductDetails(extractedText);
            String category = classifyProduct(details);
            details.setCategory(category);
            details = productDetailsRepository.save(details);

            return details;
        } catch (Exception e) {
            logger.error("Error processing image: {}", e.getMessage(), e);
            throw new IOException("Failed to process image due to OCR or parsing error", e);
        } finally {
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    tempFile.deleteOnExit();
                }
            }
        }
    }

    private String doOCR(File imageFile) {
        Tesseract tesseract = new Tesseract();
        // TODO: Ensure tessdata path is configured correctly (e.g., via env var or config)
        // tesseract.setDatapath("/path/to/tessdata");
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            logger.error("OCR processing error: {}", e.getMessage(), e);
            return ""; // Return empty string or throw exception?
        }
    }

    // Very naive parsing logic - needs improvement based on expected image content
    private ProductDetails parseTextToProductDetails(String text) {
        ProductDetails details = new ProductDetails();
        details.setId(UUID.randomUUID());
        details.setOcrText(text != null ? text : "");

        String name = "Unknown Product";
        BigDecimal price = BigDecimal.ZERO;

        if (text != null) {
            String[] lines = text.split("\\r?\\n");
            for (String line : lines) {
                String lower = line.toLowerCase();
                if (lower.startsWith("name:")) {
                    name = line.substring(line.indexOf(":") + 1).trim();
                } else if (lower.startsWith("price:")) {
                    String val = line.substring(line.indexOf(":") + 1).trim();
                    try {
                        price = new BigDecimal(val.replaceAll("[^\\d.]", "")); // Attempt to clean price string
                    } catch (NumberFormatException e) {
                        logger.warn("Could not parse price value: {}", val);
                    }
                }
                // Add more specific patterns based on expected tag format
            }
        }

        details.setName(name);
        details.setPrice(price);
        details.setDescription(text != null ? text : ""); // Store full OCR text as description

        return details;
    }

    // Very naive classification - needs improvement
    private String classifyProduct(ProductDetails details) {
        if (details.getName() == null) return "General";
        String name = details.getName().toLowerCase();
        if (name.contains("shirt") || name.contains("tee")) {
            return "Apparel - Top";
        } else if (name.contains("shoe") || name.contains("sneaker") || name.contains("boot")) {
            return "Footwear";
        } else if (name.contains("pant") || name.contains("jean") || name.contains("short")) {
            return "Apparel - Bottom";
        } // Add more classifications
        return "General";
    }
} 