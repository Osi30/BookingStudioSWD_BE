package com.studio.booking.dtos.response;

import com.studio.booking.enums.PaymentMethod;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String id;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private PaymentType paymentType;
    private LocalDateTime paymentDate;
    private Double amount;

    // Related info
    private String bookingId;
    private String bookingStatus;
    private String accountEmail;
    private String accountName;
}
