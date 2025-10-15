package com.studio.booking.mappers;

import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Payment;

public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);
}
