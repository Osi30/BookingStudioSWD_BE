package com.studio.booking.services.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.studio.booking.dtos.request.EmailRequest;
import com.studio.booking.exceptions.exceptions.EmailException;
import com.studio.booking.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${SENDGRID_API_KEY}")
    private String senderApiKey;

    @Value("${EMAIL_FROM}")
    private String emailFrom;

    private final SpringTemplateEngine templateEngine;

    @Async("threadPoolTaskExecutor_FFE")
    @Override
    public void sendHtmlEmail(EmailRequest request) {
        try {
            // Url for button href variable
            String verifyUrl = frontEndUrl + verifyTokenApi + request.getVerifyToken();

            // Act as a Map for put data to html file
            Context context = new Context();
            for (Map.Entry<String, String> entry : request.getAttributes().entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }

            // Get template
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

            // Set email properties
            Email from = new Email(emailFrom);
            Email to = new Email(request.getTo());
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, request.getEmailTemplate().getSubject(), to, content);

            // Set email request
            SendGrid sendGrid = new SendGrid(senderApiKey);
            Request senderRequest = new Request();
            senderRequest.setMethod(Method.POST);
            senderRequest.setEndpoint("mail/send");
            senderRequest.setBody(mail.build());

            // Request api
            Response response = sendGrid.api(senderRequest);

            if (response.getStatusCode() >= 400) {
                throw new EmailException("SendGrid API call failed with status: " + response.getStatusCode() + " body: " + response.getBody());
            }
        } catch (Exception e) {
            throw new EmailException(e.getMessage());
        }
    }
}
