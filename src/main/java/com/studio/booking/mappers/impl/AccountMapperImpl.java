package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.exceptions.exceptions.AuthException;
import com.studio.booking.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountMapperImpl implements AccountMapper {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account toAccount(AuthRequest authRequest) {
        Account account = modelMapper.map(authRequest, Account.class);

        switch (authRequest.getAuthType()) {
            case GOOGLE:
                account.setStatus(AccountStatus.ACTIVE);
                break;
            default:
                if (authRequest.getPassword() == null){
                    throw new AuthException("Password is required");
                }
                account.setPassword(passwordEncoder.encode(authRequest.getPassword()));
                account.setStatus(authRequest.getEmail() == null ? AccountStatus.ACTIVE : AccountStatus.INACTIVE);
                break;
        }

        return account;
    }

    @Override
    public Account updateAccount(AuthRequest request, Account existedAccount) {
        Optional.ofNullable(request.getEmail()).ifPresent(existedAccount::setEmail);
        Optional.ofNullable(request.getFullName()).ifPresent(existedAccount::setFullName);
        Optional.ofNullable(request.getPhoneNumber()).ifPresent(existedAccount::setPhoneNumber);
        Optional.ofNullable(request.getUsername()).ifPresent(existedAccount::setUsername);
        Optional.ofNullable(request.getAccountStatus()).ifPresent(existedAccount::setStatus);

        if (request.getPassword() != null) {
            existedAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return existedAccount;
    }

    @Override
    public AccountResponse toAccountResponse(Account account) {
        return modelMapper.map(account, AccountResponse.class);
    }
}
