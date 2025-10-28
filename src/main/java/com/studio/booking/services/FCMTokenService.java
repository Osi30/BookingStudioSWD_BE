package com.studio.booking.services;

public interface FCMTokenService {
    void registerToken(String accountId, String fcmToken);
}
