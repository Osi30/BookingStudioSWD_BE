package com.studio.booking.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.studio.booking.dtos.request.AuthRequest;

public interface AuthService {
    String register(AuthRequest authRequest);

    String login(AuthRequest authRequest);

    String requestResetPassword(String email);

    String generateOauthURL(String loginType);

    String exchangeCodeForToken(String code) throws JsonProcessingException;

    JsonNode getUserInfo(String accessToken) throws JsonProcessingException;
}
