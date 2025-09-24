package com.studio.booking.services;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.TokenType;

import java.util.Map;

public interface VerifyTokenService {
    String sendToken(Account account, TokenType tokenType);
}
