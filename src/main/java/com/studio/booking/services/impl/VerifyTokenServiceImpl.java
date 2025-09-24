package com.studio.booking.services.impl;

import com.studio.booking.entities.Account;
import com.studio.booking.entities.VerifyToken;
import com.studio.booking.enums.TokenType;
import com.studio.booking.repositories.VerifyTokenRepo;
import com.studio.booking.services.VerifyTokenService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyTokenServiceImpl implements VerifyTokenService {
    @Value("${TOKEN_EMAIL}")
    private Long tokenEmailTime;

    @Value("${TOKEN_RESET_PASSWORD}")
    private Long tokenResetPasswordTime;

    private final VerifyTokenRepo verifyTokenRepo;

    @Override
    public String sendToken(Account account, TokenType tokenType) {
        // Generate email token for verification
        VerifyToken verifyToken = generateToken(account, tokenType);

        return verifyTokenRepo.save(verifyToken).getToken();
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
}
