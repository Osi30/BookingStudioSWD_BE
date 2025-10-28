package com.studio.booking.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.FcmTokenRequest;
import com.studio.booking.services.AuthService;
import com.studio.booking.services.FCMTokenService;
import com.studio.booking.services.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final FCMTokenService fcmTokenService;

    @GetMapping("/oauth-login")
    public ResponseEntity<BaseResponse> oauthLogin() {
        // URL redirect to Social Authorization Server
        String url = authService.generateOauthURL();

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Google Redirect Url")
                .data(url)
                .build();
        return ResponseEntity.ok(baseResponse);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<BaseResponse> callbackGoogle(
            @RequestParam("code") String code
    ) throws JsonProcessingException {
        // 1. Provide authorization code for an access token of user's account from Google's API
        String accessToken = authService.exchangeCodeForToken(code);
        // 2. Retrieve user info using access token
        JsonNode userInfo = authService.getUserInfo(accessToken);
        String email = userInfo.get("email").asText();
        String name = userInfo.get("name").asText();

        String response = authService.loginGoogle(email, name);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Token Response")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/google/android-callback")
    public ResponseEntity<BaseResponse> callbackAndroid(
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Token is authenticated by Spring Security
        // Extract to get info
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        String response = authService.loginGoogle(email, name);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Token Response")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/register-fcm-token")
    public ResponseEntity<BaseResponse> registerFCMToken(
            @RequestHeader("Authorization") String token,
            @RequestBody FcmTokenRequest request) {
        String accountId = jwtService.getIdentifierFromToken(token);
        fcmTokenService.registerToken(accountId, request.getFcmToken());
        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Register fcm token success")
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
