package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.EmailRequest;
import com.studio.booking.entities.Account;
import com.studio.booking.entities.VerifyToken;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.enums.EmailTemplate;
import com.studio.booking.enums.TokenType;
import com.studio.booking.exceptions.exceptions.AuthException;
import com.studio.booking.repositories.VerifyTokenRepo;
import com.studio.booking.services.EmailService;
import com.studio.booking.services.VerifyTokenService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyTokenServiceImpl implements VerifyTokenService {
    @Value("${TOKEN_EMAIL}")
    private Long tokenEmailTime;

    @Value("${TOKEN_RESET_PASSWORD}")
    private Long tokenResetPasswordTime;

    private final EmailService emailService;
    private final VerifyTokenRepo verifyTokenRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String sendToken(Account account, TokenType tokenType) {
        // Generate email token for verification
        VerifyToken verifyToken = generateToken(account, tokenType);

        // Choose and send email template
        return switch (tokenType) {
            case VERIFY_EMAIL -> verifyEmail(account, verifyToken);
            default -> verifyResetPassword(account, verifyToken);
        };
    }

    @Override
    public boolean verifyToken(String token, Map<String, Object> data) {
        VerifyToken verifyToken = verifyTokenRepo.findByToken(token);

        if (!validateToken(verifyToken)) {
            return false;
        }

        verifyToken.setIsVerified(true);

        switch (verifyToken.getTokenType()) {
            case VERIFY_EMAIL:
                verifyToken.getAccount().setStatus(AccountStatus.ACTIVE);
                break;
            default:
                String password = data.get("password").toString();
                String confirmPassword = data.get("confirmPassword").toString();

                if (!Validation.isNullOrEmpty(password) && !Validation.isNullOrEmpty(confirmPassword)
                        && password.equals(confirmPassword)) {
                    verifyToken.getAccount().setPassword(passwordEncoder.encode(password));
                } else {
                    throw new AuthException("Incorrect password or confirm password");
                }
                break;
        }

        verifyTokenRepo.save(verifyToken);
        return true;
    }

    public String verifyEmail(Account account, VerifyToken verifyToken) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fullName", account.getFullName());
        attributes.put("tokenTime", tokenEmailTime.toString());

        // Construct and send to account email
        emailService.sendHtmlEmail(EmailRequest.builder()
                .to(account.getEmail())
                .attributes(attributes)
                .verifyToken(verifyToken.getToken())
                .emailTemplate(EmailTemplate.VERIFY_EMAIL)
                .build());

        return "Please verify your email";
    }

    public String verifyResetPassword(Account account, VerifyToken verifyToken) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fullName", account.getFullName());
        attributes.put("tokenTime", tokenResetPasswordTime.toString());

        // Construct and send to account email
        emailService.sendHtmlEmail(EmailRequest.builder()
                .to(account.getEmail())
                .attributes(attributes)
                .verifyToken(verifyToken.getToken())
                .emailTemplate(EmailTemplate.RESET_PASSWORD)
                .build());

        return "Please verify your email to reset password";
    }

    public VerifyToken generateToken(Account account, TokenType tokenType) {
        // Find token in Account
        VerifyToken existedToken = null;

        if (Validation.isValidCollection(account.getVerifyTokens())) {
            existedToken = account.getVerifyTokens()
                    .stream().filter(t -> t.getTokenType().equals(tokenType))
                    .findFirst().orElse(null);
        }

        Long tokenTimeToLive = switch (tokenType) {
            case VERIFY_EMAIL -> tokenEmailTime;
            default -> tokenResetPasswordTime;
        };

        // Update token if existed
        if (existedToken != null) {
            existedToken.setToken(UUID.randomUUID().toString());
            existedToken.setExpiryDate(LocalDateTime.now().plusMinutes(tokenTimeToLive));
            return verifyTokenRepo.save(existedToken);
        }

        // Create new token
        VerifyToken token = VerifyToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusMinutes(tokenTimeToLive))
                .account(account)
                .tokenType(tokenType)
                .isVerified(false)
                .build();

        return verifyTokenRepo.save(token);
    }

    private boolean validateToken(VerifyToken verifyToken) {
        if (verifyToken == null) {
            return false;
        }
        return !verifyToken.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
