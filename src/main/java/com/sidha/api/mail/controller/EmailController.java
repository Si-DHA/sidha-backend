package com.sidha.api.mail.controller;

import com.sidha.api.mail.dto.EmailRequest;
import com.sidha.api.mail.service.EmailService;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailController {
    private final EmailService emailService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendMail(@RequestBody EmailRequest request) throws MessagingException {
        emailService.sendMail(request);
    }
}