package com.studio.booking.dtos.request;

import com.studio.booking.enums.PaymentMethod;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentRequest {
    private String bookingId;
    private PaymentMethod paymentMethod;
    private Double amount;
    private PaymentType paymentType;
    private PaymentStatus status;
}
