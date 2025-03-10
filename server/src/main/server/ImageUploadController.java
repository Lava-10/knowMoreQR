package com.knowMoreQR.server;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    private final ImageProcessingService imageProcessingService;

    // Use constructor injection
    public ImageUploadController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    // POST /api/upload
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("image") MultipartFile file) {
        try {
            // 1) Send to service for OCR + classification + saving
            ProductDetails details = imageProcessingService.processImage(file);

            // 2) Return the product details (containing OCR text, classification, etc.)
            return ResponseEntity.ok(details);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Unexpected error: " + e.getMessage());
        }
    }
}
