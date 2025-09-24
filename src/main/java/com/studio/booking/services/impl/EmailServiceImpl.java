package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.EmailRequest;
import com.studio.booking.exceptions.exceptions.EmailException;
import com.studio.booking.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${FRONT_END_URL}")
    private String frontEndUrl;

    @Value("${VERIFY_API}")
    private String verifyTokenApi;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("threadPoolTaskExecutor_FFE")
    @Override
    public void sendHtmlEmail(EmailRequest request) {
        try {
            // Support HTML content types
            MimeMessage message = mailSender.createMimeMessage();

            // Utility class to indicate email is a "multipart" message (contains both text and attachments)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Url for button href variable
            String verifyUrl = frontEndUrl + verifyTokenApi + request.getVerifyToken();

            // Act as a Map for put data to html file
            Context context = new Context();
            for (Map.Entry<String, String> entry : request.getAttributes().entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }

            switch (request.getEmailTemplate()) {
                case VERIFY_EMAIL:
                    context.setVariable("verifyUrl", verifyUrl);
                    break;
                default:
                    context.setVariable("resetUrl", verifyUrl);
                    break;
            }

            // Find & generate html file and set data from context
            String htmlContent = templateEngine.process(request.getEmailTemplate().getCode(), context);

            // Set receiver, subject and content body
            helper.setTo(request.getTo());
            helper.setSubject(request.getEmailTemplate().getSubject());
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailException(e.getMessage());
        }
    }
}
