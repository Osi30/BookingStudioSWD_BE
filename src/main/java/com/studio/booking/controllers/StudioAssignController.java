package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.request.UpdateAdditionalTimeRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.services.StudioAssignService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studio-assigns")
@RequiredArgsConstructor
public class StudioAssignController {
    private final StudioAssignService service;
    private final StudioAssignService studioAssignService;

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

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<BaseResponse> getByBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get assignments by booking successfully!")
                .data(service.getByBooking(bookingId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/studio/{studioId}")
    public ResponseEntity<BaseResponse> getByStudio(@PathVariable String studioId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get assignments by studio successfully!")
                .data(service.getByStudio(studioId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody StudioAssignRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create studio assignment successfully!")
                .data(service.create(req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(
            @PathVariable String id,
            @RequestBody StudioAssignRequest req
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update studio assignment successfully!")
                .data(service.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(service.delete(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/status/{id}")
    public ResponseEntity<BaseResponse> updateStatus(
            @PathVariable String id,
            @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("Update studio assign status successfully!")
                        .data(studioAssignService.updateStatus(id, request))
                        .build()
        );
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PatchMapping("/{assignId}/addition-time")
    public ResponseEntity<BaseResponse> addAdditionTime(
            @PathVariable String assignId,
            @RequestBody UpdateAdditionalTimeRequest req
    ) {
        var data = service.addAdditionTime(assignId, req);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("Add addition time successfully!")
                        .data(data)
                        .build()
        );
    }
}
