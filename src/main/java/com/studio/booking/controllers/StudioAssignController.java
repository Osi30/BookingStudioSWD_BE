package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.services.StudioAssignService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/studio-assigns")
@RequiredArgsConstructor
public class StudioAssignController {
    private final StudioAssignService service;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all studio assignments successfully!")
                .data(service.getAll())
                .build());
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<BaseResponse> getByBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get assignments by booking successfully!")
                .data(service.getByBooking(bookingId))
                .build());
    }

    @GetMapping("/studio/{studioId}")
    public ResponseEntity<BaseResponse> getByStudio(@PathVariable String studioId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get assignments by studio successfully!")
                .data(service.getByStudio(studioId))
                .build());
    }

    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody StudioAssignRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create studio assignment successfully!")
                .data(service.create(req))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable String id,
                                               @RequestBody StudioAssignRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update studio assignment successfully!")
                .data(service.update(id, req))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(service.delete(id))
                .build());
    }
}
