package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.services.CloudinaryService;
import com.studio.booking.services.StudioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/studios")
@RequiredArgsConstructor
public class StudioController {
    private final StudioService studioService;

    @GetMapping
    public ResponseEntity<BaseResponse> getAll(
            @RequestParam(required = false) String studioTypeId
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all studios successfully!")
                .data(studioService.getAll(studioTypeId))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get studio successfully!")
                .data(studioService.getById(id))
                .build());
    }

    //    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BaseResponse> create(
            @RequestPart StudioRequest req,
            @RequestPart(required = false) MultipartFile imageFile
    ) throws IOException {
        req.setImage(imageFile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Create studio successfully!")
                        .data(studioService.create(req))
                        .build());
    }

    //    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable String id,
                                               @RequestBody StudioRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update studio successfully!")
                .data(studioService.update(id, req))
                .build());
    }

    //    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(studioService.delete(id))
                .build());
    }

    //    @SecurityRequirement(name = "BearerAuth")
//    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<BaseResponse> restore(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(studioService.restore(id))
                .build());
    }
}
