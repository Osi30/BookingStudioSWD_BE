package com.studio.booking.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface AuthService {

    String loginGoogle(String email, String name);

    String generateOauthURL();

    String exchangeCodeForToken(String code) throws JsonProcessingException;

    JsonNode getUserInfo(String accessToken) throws JsonProcessingException;
}
