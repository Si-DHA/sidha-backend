package com.sidha.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sidha.api.model.FAQ;
import com.sidha.api.repository.FAQDb;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FAQServiceImpl implements FAQService {

    @Autowired
    private FAQDb faqDb;

    @Override
    public List<FAQ> getAllFAQs() {
        return faqDb.findAll().stream()
                .filter(faqItem -> !faqItem.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public FAQ getFAQById(Long id) {
        return faqDb.findById(id)
                .filter(faqItem -> !faqItem.isDeleted())
                .orElse(null);
    }

    @Override
    public FAQ createFAQ(FAQ faq) {
        return faqDb.save(faq);
    }

    @Override
    public FAQ updateFAQ(Long id, FAQ faqDetails) {
        FAQ existingFAQ = faqDb.findById(id)
                .filter(faqItem -> !faqItem.isDeleted())
                .orElse(null);

        if (existingFAQ == null) {
            return null;
        }

        // Check if the question is not empty, then update it
        if (faqDetails.getQuestion() != null && !faqDetails.getQuestion().isEmpty()) {
            existingFAQ.setQuestion(faqDetails.getQuestion());
        }

        // Check if the answer is not empty, then update it
        if (faqDetails.getAnswer() != null && !faqDetails.getAnswer().isEmpty()) {
            existingFAQ.setAnswer(faqDetails.getAnswer());
        }

        return faqDb.save(existingFAQ);
    }

    @Override
    public void deleteFAQ(Long id) {
        FAQ existingFAQ = faqDb.findById(id)
                .filter(faqItem -> !faqItem.isDeleted())
                .orElse(null);

        if (existingFAQ != null) {
            existingFAQ.setDeleted(true);
            faqDb.save(existingFAQ);
        }
    }

}
