package com.studio.booking.services.impl;

import com.studio.booking.entities.Account;
import com.studio.booking.entities.FcmToken;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.repositories.AccountRepo;
import com.studio.booking.repositories.FcmTokenRepo;
import com.studio.booking.services.FCMTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FCMTokenServiceImpl implements FCMTokenService {
    private final FcmTokenRepo fcmTokenRepo;
    private final AccountRepo accountRepo;

    @Override
    public void registerToken(String accountId, String fcmToken) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found."));

        Optional<FcmToken> existingToken = fcmTokenRepo.findByToken(fcmToken);

        if (existingToken.isPresent()) {
            FcmToken token = existingToken.get();
            token.setAccount(account);
            token.setCreatedAt(LocalDateTime.now());
            fcmTokenRepo.save(token);
        } else {
            fcmTokenRepo.save(FcmToken.builder()
                    .token(fcmToken)
                    .account(account)
                    .deviceType("Android")
                    .build());
        }
    }
}
