package com.sidha.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sidha.api.model.FAQ;
import com.sidha.api.service.FAQService;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
public class FAQController {

    @Autowired
    private FAQService faqService;

    // Get all FAQs
    @GetMapping
    public ResponseEntity<List<FAQ>> getAllFAQs() {
        List<FAQ> faqs = faqService.getAllFAQs();
        if (faqs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(faqs);
    }

    // Get a single FAQ by ID
    @GetMapping("/{id}")
    public ResponseEntity<FAQ> getFAQById(@PathVariable Long id) {
        FAQ faq = faqService.getFAQById(id);
        if (faq == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(faq);
    }

    // Create a new FAQ
    @PostMapping("/create")
    public ResponseEntity<FAQ> createFAQ(@RequestBody FAQ faq) {
        FAQ createdFAQ = faqService.createFAQ(faq);
        if (createdFAQ == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(createdFAQ);
    }

    // Update an existing FAQ
    @PutMapping("/update/{id}")
    public ResponseEntity<FAQ> updateFAQ(@PathVariable Long id, @RequestBody FAQ faqDetails) {
        FAQ updatedFAQ = faqService.updateFAQ(id, faqDetails);
        if (updatedFAQ == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(updatedFAQ);
    }

    // Soft delete an FAQ (mark as deleted)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFAQ(@PathVariable Long id) {
        try {
            faqService.deleteFAQ(id);
            return ResponseEntity.ok("FAQ is deleted!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting FAQ: " + e.getMessage());
        }
    }
}