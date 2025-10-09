package com.studio.booking.mappers;

import com.studio.booking.dtos.response.AdminPaymentResponse;
import com.studio.booking.entities.Payment;

public interface AdminPaymentMapper {
    AdminPaymentResponse toResponse(Payment payment);
}
