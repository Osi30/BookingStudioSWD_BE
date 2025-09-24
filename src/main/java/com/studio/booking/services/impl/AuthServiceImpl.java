package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.entities.Account;
import com.studio.booking.services.AccountService;
import com.studio.booking.services.AuthService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountService accountService;

    @Override
    public String register(AuthRequest authRequest) {
        // Validation
        if (Validation.isNullOrEmpty(authRequest.getUsername())
                && Validation.isNullOrEmpty(authRequest.getEmail())
                && Validation.isNullOrEmpty(authRequest.getPhoneNumber())) {
            throw new BadCredentialsException("Required at least one field of username, email");
        }

        // Create account
        Account account = accountService.createAccount(authRequest);

        StringBuilder message = new StringBuilder("Create account successfully!");

        return message.toString();
    }
}
