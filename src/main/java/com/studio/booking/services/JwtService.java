package com.studio.booking.services;

import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateToken(Authentication authentication);
    String getIdentifierFromToken(String token);
}
