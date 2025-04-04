package com.knowMoreQR.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.knowMoreQR.server.auth.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/tags")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<List<Tag>> all() {
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> tag(@PathVariable("id") UUID id) {
        Optional<Tag> tagOpt = tagRepository.findById(id);
        if (tagOpt.isPresent()) {
            return ResponseEntity.ok(tagOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Tag> create(@RequestBody Tag newTag) {
        newTag.setId(UUID.randomUUID());
        
        Tag savedTag = tagRepository.save(newTag);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable("id") UUID id, @RequestBody Tag updatedTag) {
        if (!tagRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedTag.setId(id);
        
        Tag savedTag = tagRepository.save(updatedTag);
        return ResponseEntity.ok(savedTag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        if (!tagRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-tags")
    public ResponseEntity<List<Tag>> getCompanyTags(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long companyLoginId = userDetails.getUserId();
        String userType = userDetails.getUserType();

        if (!"company".equalsIgnoreCase(userType)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        logger.warn("Fetching all tags and filtering by placeholder companyId 'temp-company-id' for /my-tags. Implement efficient query!");
        List<Tag> allTags = tagRepository.findAll();
        List<Tag> companyTags = allTags.stream()
            .filter(tag -> "temp-company-id".equals(tag.getCompanyId()))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(companyTags);
    }
}
