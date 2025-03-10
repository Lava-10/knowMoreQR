package com.knowMoreQR.server;

import net.sourceforge.tess4j.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ImageProcessingService {

    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    // Main entrypoint: process the file with OCR, parse, classify, save
    public ProductDetails processImage(MultipartFile multipartFile) throws IOException {
        // 1) Convert to a local File so Tess4J can read it
        File tempFile = File.createTempFile("ocr-upload-", ".jpg");
        multipartFile.transferTo(tempFile);

        // 2) Perform OCR
        String extractedText = doOCR(tempFile);

        // 3) Parse the text into some structured product details
        ProductDetails details = parseTextToProductDetails(extractedText);

        // 4) Optionally do classification (category or brand, etc.)
        String category = classifyProduct(details);
        details.setCategory(category);

        // 5) Save to Cassandra
        details = productDetailsRepository.save(details);

        // 6) Clean up temp file if you like (tempFile.deleteOnExit() or tempFile.delete())
        // Return the entity (which includes the newly assigned ID, etc.)
        return details;
    }

    private String doOCR(File imageFile) {
        Tesseract tesseract = new Tesseract();
        // If needed: tesseract.setDatapath("/path/to/tessdata");
        // tesseract.setLanguage("eng");

        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Very naive parsing logic - adapt to your text layout
    private ProductDetails parseTextToProductDetails(String text) {
        ProductDetails details = new ProductDetails();
        details.setId(UUID.randomUUID());
        details.setOcrText(text);

        // Example: look for lines "Name: X" or "Price: Y"
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            String lower = line.toLowerCase();
            if (lower.contains("name:")) {
                String val = line.substring(line.indexOf(":") + 1).trim();
                details.setName(val);
            } else if (lower.contains("price:")) {
                String val = line.substring(line.indexOf(":") + 1).trim();
                try {
                    details.setPrice(new BigDecimal(val));
                } catch (NumberFormatException e) {
                    // fallback
                    details.setPrice(BigDecimal.ZERO);
                }
            }
            // Add more patterns as needed
        }

        // If you find nothing, you can store a default or everything in name
        if (details.getName() == null) {
            details.setName("Unknown Product");
        }

        // Store the entire extracted text in the "description" if you want
        details.setDescription(text);

        return details;
    }

    private String classifyProduct(ProductDetails details) {
        // Example classification by name or text
        String name = details.getName().toLowerCase();
        if (name.contains("shirt")) {
            return "Shirt";
        } else if (name.contains("shoe")) {
            return "Shoe";
        }
        return "General";
    }
}
