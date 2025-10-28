package com.studio.booking.services;

import com.google.firebase.messaging.SendResponse;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    @Async
    void sendNotificationToUser(String accountId, String title, String body, Map<String, String> data) throws MessagingException;

    void handleFailedTokens(List<SendResponse> responses, List<String> tokens);
}
