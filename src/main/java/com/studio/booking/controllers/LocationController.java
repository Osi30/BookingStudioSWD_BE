package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.LocationRequest;
import com.studio.booking.services.LocationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @GetMapping()
    public ResponseEntity<BaseResponse> getAllLocations(
            @RequestParam(required = false) String typeId
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all locations successfully!")
                .data(locationService.getAll(typeId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<BaseResponse> createLocation(@RequestBody LocationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Create location successfully!")
                        .data(locationService.create(req))
                        .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateLocation(@PathVariable String id,
                                                       @RequestBody LocationRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update location successfully!")
                .data(locationService.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteLocation(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(locationService.delete(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<BaseResponse> restoreLocation(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(locationService.restore(id))
                .build());
    }
}
