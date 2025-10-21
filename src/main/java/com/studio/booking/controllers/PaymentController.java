package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.PaymentRequest;
import com.studio.booking.dtos.request.PaymentStatusRequest;
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

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all payments successfully!")
                .data(paymentService.getAll())
                .build());
    }

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get payment successfully!")
                .data(paymentService.getById(id))
                .build());
    }

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse> updateStatus(@PathVariable String id,
                                                     @RequestBody PaymentStatusRequest req) {
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

    @PostMapping()
    public ResponseEntity<BaseResponse> create(@RequestBody PaymentRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Create payment status successfully!")
                .data(paymentService.createPayment(req,null))
                .build());
    }
}
