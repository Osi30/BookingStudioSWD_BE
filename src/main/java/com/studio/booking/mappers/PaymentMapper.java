package com.studio.booking.mappers;

import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Payment;

public interface PaymentMapper {
    Payment toPayment(PaymentRequest paymentRequest);
    PaymentResponse toResponse(Payment payment);
}
