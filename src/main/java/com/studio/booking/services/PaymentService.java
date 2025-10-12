package com.studio.booking.services;

import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getAll();
    PaymentResponse getById(String id);
    PaymentResponse updateStatus(String id, PaymentStatusRequest req);
}
