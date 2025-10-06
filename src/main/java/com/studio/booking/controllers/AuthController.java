package com.studio.booking.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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

        String response = authService.login(userInfo);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Token Response")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
