package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountIdentifier;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.enums.AuthType;
import com.studio.booking.enums.TokenType;
import com.studio.booking.services.AccountService;
import com.studio.booking.services.AuthService;
import com.studio.booking.services.JwtService;
import com.studio.booking.services.VerifyTokenService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountService accountService;
    private final VerifyTokenService verifyTokenService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

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

        if (account.getEmail() != null) {
            // Generate and send email token for verification
            String verifyEmailMessage = verifyTokenService.sendToken(account, TokenType.VERIFY_EMAIL);
            message.append(verifyEmailMessage);
        }

        return message.toString();
    }

    @Override
    public String login(AuthRequest authRequest) {
        Authentication authentication = authenticate(authRequest, authRequest.getAuthType());
        return jwtService.generateToken(authentication);
    }

    private Authentication authenticate(AuthRequest authRequest, AuthType authType) {
        UserDetails userDetails;

        switch (authType) {
            case GOOGLE:
                Account googleAccount = accountService.getAccountByIdentifier(authRequest.getEmail(), AccountIdentifier.EMAIL);

                // Create account if not exist one
                if (googleAccount == null) {
                    authRequest.setAccountStatus(AccountStatus.ACTIVE);
                    googleAccount = accountService.createAccount(authRequest);
                }

                userDetails = new User(googleAccount.getId(), "", googleAccount.getAuthorities());
                break;
            default:
                userDetails = userDetailsService.loadUserByUsername(authRequest.getIdentifier());
                if (userDetails == null) {
                    throw new BadCredentialsException("Account not found with identifier: " + authRequest.getIdentifier());
                }
                if (!passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
                    throw new BadCredentialsException("Invalid password");
                }
                break;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
