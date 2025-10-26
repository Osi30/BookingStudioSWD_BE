package com.studio.booking.controllers;

import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.mappers.AccountMapper;
import com.studio.booking.services.AccountService;
import com.studio.booking.services.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final JwtService jwtService;

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<BaseResponse> getAllAccounts() {
        List<AccountResponse> accountResponses = accountService.getAllAccounts();

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get accounts successfully!")
                .data(accountResponses)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BaseResponse> getAccountById(
            @PathVariable String accountId
    ) {
        AccountResponse accountResponse = accountMapper
                .toAccountResponse(accountService.getAccountById(accountId));

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get account successfully!")
                .data(accountResponse)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse> getAccountProfile(
            @RequestHeader("Authorization") String token
    ) {
        String accountId = jwtService.getIdentifierFromToken(token);
        AccountResponse accountResponse = accountMapper
                .toAccountResponse(accountService.getAccountById(accountId));

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Get profile successfully!")
                .data(accountResponse)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PutMapping
    public ResponseEntity<BaseResponse> updateAccount(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody AccountRequest accountRequest
    ) {
        String accountId = jwtService.getIdentifierFromToken(token);
        AccountResponse accountResponse = accountService.updateAccount(accountRequest, accountId);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Update account successfully!")
                .data(accountResponse)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping
    public ResponseEntity<BaseResponse> deleteAccount(
            @RequestHeader(value = "Authorization") String token
    ) {
        String accountId = jwtService.getIdentifierFromToken(token);
        String messageResponse = accountService.deleteAccount(accountId);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(messageResponse)
                .data(null)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<BaseResponse> banAccount(
            @PathVariable String accountId
    ) {
        String response = accountService.banAccount(accountId);

        BaseResponse baseResponse = BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Ban account successfully!")
                .data(response)
                .build();
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("unban/{accountId}")
    public ResponseEntity<BaseResponse> unban(@PathVariable String accountId) {
        String msg = accountService.unban(accountId);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message(msg)
                .data(null)
                .build());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<BaseResponse> createAccount(
            @RequestBody AccountRequest accountRequest
    ) {
        AccountResponse response = accountService.createAccount(accountRequest);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Create account successfully!")
                .data(response)
                .build());
    }
}

