package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.StudioTypeRequest;
import com.studio.booking.services.LocationService;
import com.studio.booking.services.StudioTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class StudioTypeController {
    private final StudioTypeService studioTypeService;
    private final LocationService locationService;

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
    public ResponseEntity<BaseResponse> createStudioType(@RequestBody StudioTypeRequest req) {
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
                                                         @RequestBody StudioTypeRequest req) {
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
}
