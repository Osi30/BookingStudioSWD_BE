package com.studio.booking.services.impl;

import com.google.firebase.messaging.*;
import com.studio.booking.entities.FcmToken;
import com.studio.booking.repositories.FcmTokenRepo;
import com.studio.booking.services.NotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final FcmTokenRepo fcmTokenRepo;

    /// Send notification to multiple devices
    @Async("threadPoolTaskExecutor_FFE")
    @Override
    public void sendNotificationToUser(String accountId, String title, String body, Map<String, String> data) throws MessagingException {
        List<String> tokens = fcmTokenRepo.findAllByAccount_Id(accountId)
                .stream()
                .map(FcmToken::getToken)
                .toList();

        if (tokens.isEmpty()) {
            return;
        }

        // Set Up Notification (Title, Body)
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Setup Destination Channel
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setNotification(AndroidNotification.builder()
                        .setChannelId("booking_studio_channel")
                        .setPriority(AndroidNotification.Priority.HIGH)
                        .build())
                .build();

        // Create Multicast Message (send multiple devices/time)
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putAllData(data)
                .addAllTokens(tokens)
                .setAndroidConfig(androidConfig)
                .build();

        try {
            // Send message
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            // Delete failed token (if any)
            if (response.getFailureCount() > 0) {
                handleFailedTokens(response.getResponses(), tokens);
            }
        } catch (FirebaseMessagingException e) {
            throw new MessagingException(e.getMessage());
        }
    }

    /// Delete Failed Tokens in Database
    @Override
    public void handleFailedTokens(List<SendResponse> responses, List<String> tokens) {
        List<String> failedTokens = getFailedTokens(responses, tokens);
        fcmTokenRepo.deleteAllByTokenIsIn(failedTokens);
    }

    /// Get Failed Tokens
    private List<String> getFailedTokens(List<SendResponse> responses, List<String> tokens) {
        List<String> failedTokens = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                String failedToken = tokens.get(i);
                // NotRegistered" => không còn hợp lệ
                if (responses.get(i).getException().getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    failedTokens.add(failedToken);
                }
            }
        }
        return failedTokens;
    }
}
