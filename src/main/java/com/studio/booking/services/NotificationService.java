package com.studio.booking.services;

import com.google.firebase.messaging.SendResponse;
import com.studio.booking.dtos.dto.NotificationDTO;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface NotificationService {
    @Async
    void sendNotificationToUser(NotificationDTO req) throws MessagingException;

    void handleFailedTokens(List<SendResponse> responses, List<String> tokens);
}
