package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.PaymentCompletionStatusResponse;
import com.studio.booking.dtos.response.PaymentResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Payment;
import com.studio.booking.enums.BookingStatus;
import com.studio.booking.enums.PaymentMethod;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.enums.PaymentType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.exceptions.exceptions.PaymentException;
import com.studio.booking.mappers.PaymentMapper;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.repositories.PaymentRepo;
import com.studio.booking.services.MomoService;
import com.studio.booking.services.PaymentService;
import com.studio.booking.services.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final VnPayService vnPayService;
    private final MomoService momoService;
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
            case MOMO -> momoService.createMomoUrl(payment);
            default -> "Pay by cash successfully";
        };
    }

    @Override
    public void handlePaymentCallback(Boolean isSuccess, String paymentId) {
        PaymentStatus paymentStatus = isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        updateStatus(paymentId, PaymentStatusRequest.builder()
                .status(paymentStatus)
                .build());
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
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + id));

        if (req.getStatus() == null) {
            throw new PaymentException("Payment status cannot be null");
        }

        // Không cho đổi sang cùng một trạng thái
        if (payment.getStatus().equals(req.getStatus())) {
            throw new PaymentException("Payment already has this status: " + req.getStatus());
        }

        payment.setStatus(req.getStatus());

        // Success case
        if (req.getStatus().equals(PaymentStatus.SUCCESS)) {
            // Set booking status
            BookingStatus bookingStatus = switch (payment.getBooking().getStatus()) {
                case AWAITING_PAYMENT -> BookingStatus.CONFIRMED;
                case IN_PROGRESS -> payment.getBooking().isComplete().equals(Boolean.TRUE)
                        ? BookingStatus.COMPLETED
                        : payment.getBooking().getStatus();
                case AWAITING_REFUND -> BookingStatus.CANCELLED;
                default -> payment.getBooking().getStatus();
            };

            payment.getBooking().setStatus(bookingStatus);
        }
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
    @Transactional
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

        // Nếu đã có FINAL_PAYMENT trước đó, update thay vì tạo mới
        var maybeExistingFinal = paymentRepo
                .findTopByBooking_IdAndPaymentTypeOrderByPaymentDateDesc(bookingId, PaymentType.FINAL_PAYMENT);

        Payment finalPayment = maybeExistingFinal.orElseGet(Payment::new);

        // Gán/ cập nhật thông tin
        finalPayment.setPaymentType(PaymentType.FINAL_PAYMENT);
        finalPayment.setBooking(booking);
        finalPayment.setAmount(remaining);
        finalPayment.setPaymentMethod(method);
        finalPayment.setPaymentDate(java.time.LocalDateTime.now());

        // Nếu remaining = 0, có thể coi là đã hoàn tất
        finalPayment.setStatus(remaining <= 0.0 ? PaymentStatus.SUCCESS : PaymentStatus.PENDING);

        finalPayment = paymentRepo.save(finalPayment);
        return mapper.toResponse(finalPayment);
    }

}
