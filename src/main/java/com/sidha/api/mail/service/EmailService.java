package com.sidha.api.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sidha.api.mail.dto.EmailRequest;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Async
    public void sendMail(EmailRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromMail);
        mimeMessageHelper.setTo(request.getToEmail());
        mimeMessageHelper.setSubject(request.getSubject());

        if (request.isHTML()) {
            Context context = new Context();
            /*
             * content is the variable defined in our HTML template within the div tag
             */
            context.setVariable("content", request.getMessage());
            String processedString = templateEngine.process("new-account-email", context);

            mimeMessageHelper.setText(processedString, true);
        } else {
            mimeMessageHelper.setText(request.getMessage(), false);
        }

        mailSender.send(mimeMessage);
    }
}