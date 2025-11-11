package com.studio.booking.services;

import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.PaymentCompletionStatusResponse;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Payment;
import com.studio.booking.enums.PaymentMethod;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface PaymentService {
    Payment createPayment(PaymentRequest paymentRequest, Booking booking);
    String createPaymentUrl(Payment payment)
            throws UnsupportedEncodingException,
            NoSuchAlgorithmException,
            InvalidKeyException;
    void handlePaymentCallback(Boolean isSuccess, String paymentId);
    List<PaymentResponse> getAll();
    PaymentResponse getById(String id);
    PaymentResponse updateStatus(String id, PaymentStatusRequest req);
    List<PaymentResponse> getByBookingId(String bookingId);
    PaymentCompletionStatusResponse getCompletionStatus(String bookingId);
    PaymentResponse createFinalPayment(String bookingId, PaymentMethod method);
}
