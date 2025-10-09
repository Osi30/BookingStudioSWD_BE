package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.AdminLocationRequest;
import com.studio.booking.dtos.request.AdminStudioTypeRequest;
import com.studio.booking.services.AdminLocationService;
import com.studio.booking.services.AdminStudioTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStudioTypeLocationController {
    private final AdminStudioTypeService studioTypeService;
    private final AdminLocationService locationService;

    // ---------- STUDIO TYPE ----------

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/studio-types")
    public ResponseEntity<BaseResponse> getAllStudioTypes() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all studio types successfully!")
                .data(studioTypeService.getAll())
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/studio-types")
    public ResponseEntity<BaseResponse> createStudioType(@RequestBody AdminStudioTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Create studio type successfully!")
                        .data(studioTypeService.create(req))
                        .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/studio-types/{id}")
    public ResponseEntity<BaseResponse> updateStudioType(@PathVariable String id,
                                                         @RequestBody AdminStudioTypeRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update studio type successfully!")
                .data(studioTypeService.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/studio-types/{id}")
    public ResponseEntity<BaseResponse> deleteStudioType(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(studioTypeService.delete(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/studio-types/{id}/restore")
    public ResponseEntity<BaseResponse> restoreStudioType(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(studioTypeService.restore(id))
                .build());
    }

    // ---------- LOCATION ----------

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/locations")
    public ResponseEntity<BaseResponse> getAllLocations() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all locations successfully!")
                .data(locationService.getAll())
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/locations")
    public ResponseEntity<BaseResponse> createLocation(@RequestBody AdminLocationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Create location successfully!")
                        .data(locationService.create(req))
                        .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/locations/{id}")
    public ResponseEntity<BaseResponse> updateLocation(@PathVariable String id,
                                                       @RequestBody AdminLocationRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update location successfully!")
                .data(locationService.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/locations/{id}")
    public ResponseEntity<BaseResponse> deleteLocation(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(locationService.delete(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/locations/{id}/restore")
    public ResponseEntity<BaseResponse> restoreLocation(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(locationService.restore(id))
                .build());
    }
}
