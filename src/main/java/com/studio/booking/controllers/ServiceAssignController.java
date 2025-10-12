package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.ServiceAssignRequest;
import com.studio.booking.services.ServiceAssignService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-assigns")
@RequiredArgsConstructor
public class ServiceAssignController {
    private final ServiceAssignService service;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all service assignments successfully!")
                .data(service.getAll())
                .build());
    }

    @GetMapping("/studio-assign/{studioAssignId}")
    public ResponseEntity<BaseResponse> getByStudioAssign(@PathVariable String studioAssignId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get service assignments by studioAssign successfully!")
                .data(service.getByStudioAssign(studioAssignId))
                .build());
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<BaseResponse> getByService(@PathVariable String serviceId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get service assignments by service successfully!")
                .data(service.getByService(serviceId))
                .build());
    }

    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody ServiceAssignRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create service assignment successfully!")
                .data(service.create(req))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable String id,
                                               @RequestBody ServiceAssignRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update service assignment successfully!")
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
