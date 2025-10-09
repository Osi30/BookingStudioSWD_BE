package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.response.AdminPaymentResponse;
import com.studio.booking.entities.Payment;
import com.studio.booking.mappers.AdminPaymentMapper;
import org.springframework.stereotype.Component;

@Component
public class AdminPaymentMapperImpl implements AdminPaymentMapper {
    @Override
    public AdminPaymentResponse toResponse(Payment payment) {
        return AdminPaymentResponse.builder()
                .id(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentType(payment.getPaymentType())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .bookingId(payment.getBooking() != null ? payment.getBooking().getId() : null)
                .bookingStatus(payment.getBooking() != null ? payment.getBooking().getStatus().name() : null)
                .accountEmail(payment.getBooking() != null ? payment.getBooking().getAccount().getEmail() : null)
                .accountName(payment.getBooking() != null ? payment.getBooking().getAccount().getFullName() : null)
                .build();
    }
}
