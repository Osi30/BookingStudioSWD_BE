package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Payment;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.mappers.PaymentMapper;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.repositories.PaymentRepo;
import com.studio.booking.services.PaymentService;
import com.studio.booking.services.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final VnPayService vnPayService;
    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
    private final PaymentMapper mapper;

    @Override
    public Payment createPayment(PaymentRequest paymentRequest, Booking booking) {
        Payment payment = mapper.toPayment(paymentRequest);

        if (booking == null) {
            booking = bookingRepo.findById(payment.getId())
                    .orElseThrow(() -> new BookingException("Booking not found"));
        }

        payment.setBooking(booking);

        return paymentRepo.save(payment);
    }

    @Override
    public String createPaymentUrl(Payment payment) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        return switch (payment.getPaymentMethod()) {
            case VNPAY -> vnPayService.createVNPayUrl(payment);
//            case MOMO -> momoService.createMomoUrl(response);
            default -> "Pay by cash successfully";
        };
    }

    @Override
    public String handlePaymentCallback(Boolean isSuccess, String paymentId) {
        PaymentStatus paymentStatus = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        updateStatus(paymentId, PaymentStatusRequest.builder()
                .status(paymentStatus)
                .build());
        return "Payment updated successfully";
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse getById(String id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new AccountException("Payment not found with id: " + id));
        return mapper.toResponse(payment);
    }

    @Override
    public PaymentResponse updateStatus(String id, PaymentStatusRequest req) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new AccountException("Payment not found with id: " + id));

        if (req.getStatus() == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }

        // Không cho admin đổi sang cùng một trạng thái
        if (payment.getStatus().equals(req.getStatus())) {
            throw new IllegalArgumentException("Payment already has this status: " + req.getStatus());
        }

        payment.setStatus(req.getStatus());
        paymentRepo.save(payment);
        return mapper.toResponse(payment);
    }
}
