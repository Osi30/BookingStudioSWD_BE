package com.studio.booking.services;

import com.studio.booking.entities.Payment;

import java.io.UnsupportedEncodingException;

public interface VnPayService {
    String createVNPayUrl(Payment payment) throws UnsupportedEncodingException;

}
