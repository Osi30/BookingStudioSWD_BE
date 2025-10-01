package com.studio.booking.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.request.ResetPasswordRequest;
import com.studio.booking.enums.AuthType;
import com.studio.booking.services.AuthService;
import com.studio.booking.services.VerifyTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final VerifyTokenService verifyTokenService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(
            @RequestBody @Valid AuthRequest authRequest
    ) {
        // Save new account
        authRequest.setAuthType(AuthType.PASSWORD);
        String responseMessage = authService.register(authRequest);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message(responseMessage)
                .data(null)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(
            @RequestBody AuthRequest authRequest
    ) {
        // Generate tokens base on login type
        authRequest.setAuthType(AuthType.PASSWORD);
        String token = authService.login(authRequest);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Login account successfully!")
                .data(token)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<BaseResponse> verifyAccount(
            @RequestParam("token") String token
    ) {
        String message = verifyTokenService.verifyToken(token, null);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(null)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/oauth-login")
    public ResponseEntity<BaseResponse> oauthLogin(
            @RequestParam("login_type") String loginType
    ) {
        // URL redirect to Social Authorization Server
        String url = authService.generateOauthURL(loginType);

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
        // 3. Extract email (or name)
        String email = userInfo.get("email").asText();
        String name = userInfo.get("name").asText();
        // 4. Create Request to get Token
        AuthRequest request = AuthRequest.builder()
                .email(email)
                .fullName(name)
                .authType(AuthType.GOOGLE)
                .build();

        String response = authService.login(request);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Token Response")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse> forgotPassword(
            @RequestBody AuthRequest authRequest
    ) {
        String response = authService.requestResetPassword(authRequest.getEmail());

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Reset Password Response")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse> resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("password", resetPasswordRequest.getPassword());
        attributes.put("confirmPassword", resetPasswordRequest.getConfirmPassword());

        String response = verifyTokenService.verifyToken(resetPasswordRequest.getToken(), attributes);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Reset Password Response")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
