package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.StudioTypeRequest;
import com.studio.booking.services.StudioTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studio-types")
@RequiredArgsConstructor
public class StudioTypeController {
    private final StudioTypeService studioTypeService;

    @GetMapping()
    public ResponseEntity<BaseResponse> getAllStudioTypes() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all studio types successfully!")
                .data(studioTypeService.getAll())
                .build());
    }

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<BaseResponse> createStudioType(@RequestBody StudioTypeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Create studio type successfully!")
                        .data(studioTypeService.create(req))
                        .build());
    }

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateStudioType(@PathVariable String id,
                                                         @RequestBody StudioTypeRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update studio type successfully!")
                .data(studioTypeService.update(id, req))
                .build());
    }

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteStudioType(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(studioTypeService.delete(id))
                .build());
    }

//    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<BaseResponse> restoreStudioType(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(studioTypeService.restore(id))
                .build());
    }
}
