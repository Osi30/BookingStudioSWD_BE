package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Payment;
import com.studio.booking.enums.BookingType;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.enums.PaymentType;
import com.studio.booking.services.BookingService;
import com.studio.booking.services.JwtService;
import com.studio.booking.services.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final JwtService jwtService;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/bookings")
    public ResponseEntity<BaseResponse> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody BookingRequest bookingRequest
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String accountId = jwtService.getIdentifierFromToken(token);
        Booking booking = bookingService.createBooking(accountId, bookingRequest);

        // Payment
        Payment payment = paymentService.createPayment(PaymentRequest.builder()
                .amount(booking.getBookingType().equals(BookingType.PAY_FULL) ? booking.getTotal() : booking.getTotal() * 30D / 100D)
                .paymentMethod(bookingRequest.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .paymentType(booking.getBookingType().equals(BookingType.PAY_FULL) ? PaymentType.FULL_PAYMENT : PaymentType.DEPOSIT)
                .build(), booking);
        booking.setPayments(List.of(payment));

        // Create url
        String paymentUrl = paymentService.createPaymentUrl(payment);

        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create booking successfully!")
                .data(paymentUrl)
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/bookings/profile")
    public ResponseEntity<BaseResponse> getProfileBooking(
            @RequestHeader("Authorization") String token
    ) {
        String accountId = jwtService.getIdentifierFromToken(token);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all bookings successfully!")
                .data(bookingService.getBookingsByAccount(accountId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all bookings successfully!")
                .data(bookingService.getAll())
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/bookings/{id}")
    public ResponseEntity<BaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get booking successfully!")
                .data(bookingService.getById(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<BaseResponse> updateStatus(
            @PathVariable String id,
            @RequestBody BookingStatusRequest req
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update booking status successfully!")
                .data(bookingService.updateStatus(id, req))
                .build());
    }

    @PutMapping("bookings/{id}")
    public ResponseEntity<BaseResponse> update(
            @PathVariable String id,
            @RequestBody BookingRequest req
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update booking successfully!")
                .data(bookingService.updateBooking(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<BaseResponse> cancelBooking(
            @PathVariable String id,
            @RequestParam(required = false) String note
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(bookingService.cancelBooking(id, note))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/staff/bookings")
    public ResponseEntity<BaseResponse> getForEmployee(
            @RequestHeader("Authorization") String token

    ) {
        String accountId = jwtService.getIdentifierFromToken(token);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get bookings for employee successfully!")
                .data(bookingService.getForEmployee(accountId))
                .build());
    }
}
