package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.PriceTableRequest;
import com.studio.booking.services.PriceTableService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price-tables")
@RequiredArgsConstructor
public class PriceTableController {
    private final PriceTableService priceTableService;

    @GetMapping
    public ResponseEntity<BaseResponse> getAll() {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get all price tables successfully!")
                .data(priceTableService.getAll())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get price table successfully!")
                .data(priceTableService.getById(id))
                .build());
    }

    @GetMapping("/studio-types/{id}")
    public ResponseEntity<BaseResponse> getByStudioType(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get price table successfully!")
                .data(priceTableService.getByTypeId(id))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody PriceTableRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create price table successfully!")
                .data(priceTableService.create(req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(
            @PathVariable String id,
            @RequestBody PriceTableRequest req
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update price table successfully!")
                .data(priceTableService.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(priceTableService.delete(id))
                .build());
    }
}
