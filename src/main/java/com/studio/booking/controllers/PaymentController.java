package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.request.PaymentStatusRequest;
import com.studio.booking.dtos.response.FinalPaymentRequest;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.services.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    @Value("${FRONT_END_URL}")
    private String frontEndUrl;

    private final PaymentService paymentService;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all payments successfully!")
                .data(paymentService.getAll())
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get payment successfully!")
                .data(paymentService.getById(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse> updateStatus(
            @PathVariable String id,
            @RequestBody PaymentStatusRequest req
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update payment status successfully!")
                .data(paymentService.updateStatus(id, req))
                .build());
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<BaseResponse> handleVnPayCallback(@RequestParam Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String orderId = params.get("vnp_OrderInfo");

        String response = paymentService.handlePaymentCallback(
                "00".equals(responseCode) && "00".equals(transactionStatus),
                orderId
        );

        String redirectPath = "00".equals(responseCode) ? "/checkout/result?status=success" : "/checkout/result?status=fail";

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(frontEndUrl + redirectPath));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public ResponseEntity<BaseResponse> create(@RequestBody PaymentRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Create payment status successfully!")
                .data(paymentService.createPayment(req, null))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("staff/cash/{id}/")
    public ResponseEntity<BaseResponse> updateStatusStaff(@PathVariable String id) {
        PaymentStatusRequest req = new PaymentStatusRequest();
        req.setStatus(PaymentStatus.SUCCESS);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update payment status successfully!")
                .data(paymentService.updateStatus(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("staff/booking/{bookingId}")
    public ResponseEntity<BaseResponse> getByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("Get payments by booking successfully!")
                        .data(paymentService.getByBookingId(bookingId))
                        .build()
        );
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    //xem coi payment có done hết chưa
    @GetMapping("/booking/{bookingId}/status")
    public ResponseEntity<BaseResponse> getCompletionStatus(@PathVariable String bookingId) {
        var data = paymentService.getCompletionStatus(bookingId);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("Get payment completion status successfully!")
                        .data(data)
                        .build()
        );
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/booking/{bookingId}/final")
    public ResponseEntity<BaseResponse> createFinalPayment(@PathVariable String bookingId,
                                                           @RequestBody FinalPaymentRequest req) {
        var res = paymentService.createFinalPayment(bookingId, req.getPaymentMethod());
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("Create FINAL_PAYMENT successfully!")
                        .data(res)
                        .build()
        );
    }
}
