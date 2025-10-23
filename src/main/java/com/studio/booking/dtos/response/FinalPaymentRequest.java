package com.studio.booking.dtos.response;

import com.studio.booking.enums.PaymentMethod;
import lombok.Data;

@Data
public class FinalPaymentRequest {
    private PaymentMethod paymentMethod;
}
