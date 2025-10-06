package com.studio.booking.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.exceptions.exceptions.AuthException;
import com.studio.booking.services.AccountService;
import com.studio.booking.services.AuthService;
import com.studio.booking.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    private final AccountService accountService;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String login(JsonNode userInfo) {
        String email = userInfo.get("email").asText();
        String name = userInfo.get("name").asText();

        Account googleAccount = accountService.getAccountByEmail(email);

        // Create account if not exist one
        if (googleAccount == null) {
            googleAccount = accountService.createAccount(email, name);
        }

        if (googleAccount.getStatus().equals(AccountStatus.BANNED)){
            throw new AuthException("This account is already banned");
        }

        Authentication authentication = authenticate(googleAccount);
        return jwtService.generateToken(authentication);
    }

    /// Creates URL that redirects the user to Google's authorization server.
    @Override
    public String generateOauthURL() {
        String state = UUID.randomUUID().toString();
        String scope = "profile email";
        return "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=" + googleClientId +
                "&redirect_uri=" + URLEncoder.encode(googleRedirectUri
                , StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&state=" + state;
    }

    /// Exchange authorization code from user for access token to user google's account
    @Override
    public String exchangeCodeForToken(String code) throws JsonProcessingException {
        // 1. Google's API token endpoint
        String tokenUrl = "https://oauth2.googleapis.com/token";
        // 2. Setup Header that the request body will be sent as URL-encoded form data.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 3. Setup Request Parameters
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", googleClientId);
        map.add("client_secret", googleClientSecret);
        map.add("redirect_uri", googleRedirectUri);
        map.add("grant_type", "authorization_code");
        // 4. Create object representing HTTP request
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        // 5. Send POST request to token URL
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        // 6. Parse JSON response body to JsonNode object
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        // 7. Return accessToken as a String
        return responseJson.get("access_token").asText();
    }

    /// Get user google account info by sending access token
    @Override
    public JsonNode getUserInfo(String accessToken) throws JsonProcessingException {
        // 1. Google's API user info endpoint
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        // 2. Setup Request Header with Access Token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        // 3. Create object representing HTTP request
        HttpEntity<String> request = new HttpEntity<>(headers);
        // 5. Send GET request to URL
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl
                , HttpMethod.GET, request, String.class);
        // 6. Parse JSON response body to JsonNode object and return JSON response body
        return objectMapper.readTree(response.getBody());
    }

    private Authentication authenticate(Account account) {
        UserDetails userDetails = new User(account.getId(), "", account.getAuthorities());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
