package com.studio.booking.dtos.request;

import com.studio.booking.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPaymentStatusRequest {
    private PaymentStatus status;
}
