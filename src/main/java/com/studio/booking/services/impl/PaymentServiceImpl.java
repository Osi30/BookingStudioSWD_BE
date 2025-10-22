package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.PaymentCompletionStatusResponse;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Payment;
import com.studio.booking.enums.PaymentMethod;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.enums.PaymentType;
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

    @Override
    public List<PaymentResponse> getByBookingId(String bookingId) {
        bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + bookingId));

        return paymentRepo.findAllByBooking_IdOrderByPaymentDateDesc(bookingId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PaymentCompletionStatusResponse getCompletionStatus(String bookingId) {
        var booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + bookingId));

        var payments = paymentRepo.findAllByBooking_IdOrderByPaymentDateDesc(bookingId);

        int total = payments.size();
        int pending = 0, success = 0, failed = 0;
        double amountPaid = 0D;

        for (var p : payments) {
            if (p.getStatus() == PaymentStatus.PENDING) pending++;
            else if (p.getStatus() == PaymentStatus.SUCCESS) {
                success++;
                amountPaid += (p.getAmount() == null ? 0D : p.getAmount());
            } else if (p.getStatus() == PaymentStatus.FAILED) failed++;
        }

        boolean allSettled = (pending == 0);
        boolean fullyPaid = amountPaid >= (booking.getTotal() == null ? 0D : booking.getTotal());

        return PaymentCompletionStatusResponse.builder()
                .bookingId(bookingId)
                .totalPayments(total)
                .pendingCount(pending)
                .successCount(success)
                .failedCount(failed)
                .amountPaid(amountPaid)
                .bookingTotal(booking.getTotal() == null ? 0D : booking.getTotal())
                .allSettled(allSettled)
                .fullyPaid(fullyPaid)
                .build();
    }

    @Override
    public PaymentResponse createFinalPayment(String bookingId, PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        var booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + bookingId));

        double bookingTotal = booking.getTotal() == null ? 0D : booking.getTotal();

        var countedTypes = java.util.EnumSet.of(
                PaymentType.FULL_PAYMENT,
                PaymentType.ADDITION_PAYMENT,
                PaymentType.DEPOSIT
        );

        double paidSuccess = paymentRepo.sumAmountByBookingAndTypesAndStatus(
                bookingId, countedTypes, PaymentStatus.SUCCESS
        );

        double remaining = Math.max(bookingTotal - paidSuccess, 0D);

        var finalPayment = new Payment();
        finalPayment.setPaymentType(PaymentType.FINAL_PAYMENT);
        finalPayment.setStatus(PaymentStatus.PENDING);
        finalPayment.setAmount(remaining);
        finalPayment.setBooking(booking);
        finalPayment.setPaymentDate(java.time.LocalDateTime.now());
        finalPayment.setPaymentMethod(method);

        finalPayment = paymentRepo.save(finalPayment);
        return mapper.toResponse(finalPayment);
    }

}
