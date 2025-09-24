package com.studio.booking.services;

import com.studio.booking.dtos.request.EmailRequest;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendHtmlEmail(EmailRequest request);
}
