package com.sidha.api.service;

import java.util.List;

import com.sidha.api.model.FAQ;

public interface FAQService {
    List<FAQ> getAllFAQs();

    FAQ getFAQById(Long id);

    FAQ createFAQ(FAQ faq);

    FAQ updateFAQ(Long id, FAQ faqDetails);

    void deleteFAQ(Long id);
}
