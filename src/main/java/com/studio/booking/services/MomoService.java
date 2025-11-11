package com.studio.booking.services;

import com.studio.booking.entities.Payment;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MomoService {
    String createMomoUrl(Payment payment) throws NoSuchAlgorithmException, InvalidKeyException;

}
