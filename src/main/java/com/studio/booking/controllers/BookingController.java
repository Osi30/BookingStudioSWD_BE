package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.services.BookingService;
import com.studio.booking.services.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<BaseResponse> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody BookingRequest bookingRequest
    ) {
        String accountId = jwtService.getIdentifierFromToken(token);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create booking successfully!")
                .data(bookingService.createBooking(accountId, bookingRequest))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all bookings successfully!")
                .data(bookingService.getAll())
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get booking successfully!")
                .data(bookingService.getById(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse> updateStatus(@PathVariable String id,
                                                     @RequestBody BookingStatusRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update booking status successfully!")
                .data(bookingService.updateStatus(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BaseResponse> cancelBooking(@PathVariable String id,
                                                      @RequestParam(required = false) String note) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(bookingService.cancelBooking(id, note))
                .build());
    }
}
