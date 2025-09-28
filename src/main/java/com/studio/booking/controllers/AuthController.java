package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.enums.AuthType;
import com.studio.booking.services.AuthService;
import com.studio.booking.services.VerifyTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BaseResponse> verifyAccount(@RequestParam("token") String token) {
        String message = verifyTokenService.verifyToken(token, null);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(null)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
