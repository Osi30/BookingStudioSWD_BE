package com.studio.booking.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCompletionStatusResponse {
    private String bookingId;

    private int totalPayments;
    private int pendingCount;
    private int successCount;
    private int failedCount;

    // tổng amount của các payment SUCCESS
    private double amountPaid;
    // total hiện tại của booking
    private double bookingTotal;

    //true nếu không còn payment PENDING
    private boolean allSettled;

    //true nếu amountPaid >= bookingTotal
    private boolean fullyPaid;
}
