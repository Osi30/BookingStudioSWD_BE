package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Payment;
import com.studio.booking.mappers.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMapperImpl implements PaymentMapper {
    private final ModelMapper modelMapper;

    @Override
    public Payment toPayment(PaymentRequest paymentRequest) {
        return modelMapper.map(paymentRequest, Payment.class);
    }

    @Override
    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
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
