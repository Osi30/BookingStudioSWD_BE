package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.PriceRuleRequest;
import com.studio.booking.services.PriceRuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price-rules")
@RequiredArgsConstructor
public class PriceRuleController {
    private final PriceRuleService ruleService;

    @GetMapping("/item/{itemId}")
    public ResponseEntity<BaseResponse> getByItemId(@PathVariable String itemId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get rules by item successfully!")
                .data(ruleService.getByItemId(itemId))
                .build());
    }

    @GetMapping()
    public ResponseEntity<BaseResponse> getByStudioAndTableId(
            @RequestParam String studioTypeId,
            @RequestParam String priceTableId
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get rules by item successfully!")
                .data(ruleService.getByTableAndType(priceTableId, studioTypeId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody PriceRuleRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create rule successfully!")
                .data(ruleService.create(req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(
            @PathVariable String id,
            @RequestBody PriceRuleRequest req
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update rule successfully!")
                .data(ruleService.update(id, req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> delete(@PathVariable String id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(ruleService.delete(id))
                .build());
    }
}
