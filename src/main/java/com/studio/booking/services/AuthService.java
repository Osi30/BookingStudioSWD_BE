package com.studio.booking.services;

import com.studio.booking.dtos.request.AuthRequest;

public interface AuthService {
    String register(AuthRequest authRequest);

    String login(AuthRequest authRequest);
}
