package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.AdminPriceRuleRequest;
import com.studio.booking.services.AdminPriceRuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pricerules")
@RequiredArgsConstructor
public class AdminPriceRuleController {
    private final AdminPriceRuleService ruleService;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/item/{itemId}")
    public ResponseEntity<BaseResponse> getByItemId(@PathVariable String itemId) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get rules by item successfully!")
                .data(ruleService.getByItemId(itemId))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BaseResponse> create(@RequestBody AdminPriceRuleRequest req) {
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Create rule successfully!")
                .data(ruleService.create(req))
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> update(@PathVariable String id,
                                               @RequestBody AdminPriceRuleRequest req) {
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
