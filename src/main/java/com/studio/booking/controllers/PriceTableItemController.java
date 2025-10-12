package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.PriceTableItemRequest;
import com.studio.booking.services.PriceTableItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pricetable-items")
@RequiredArgsConstructor
public class PriceTableItemController {
    private final PriceTableItemService itemService;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/table/{tableId}")
    public ResponseEntity<BaseResponse> getByTableId(@PathVariable String tableId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get items by table successfully!")
                .data(itemService.getByTableId(tableId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody PriceTableItemRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create item successfully!")
                .data(itemService.create(req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable String id,
                                               @RequestBody PriceTableItemRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update item successfully!")
                .data(itemService.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(itemService.delete(id))
                .build());
    }
}
