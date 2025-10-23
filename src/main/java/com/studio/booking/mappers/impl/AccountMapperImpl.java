package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountMapperImpl implements AccountMapper {
    private final ModelMapper modelMapper;

    @Override
    public Account toAccount(AccountRequest req) {
        Account account = modelMapper.map(req, Account.class);
        account.setStatus(req.getStatus() == null ? AccountStatus.ACTIVE : req.getStatus());
        return account;
    }

    @Override
    public Account updateAccount(AccountRequest request, Account existedAccount) {
        Optional.ofNullable(request.getStatus()).ifPresent(existedAccount::setStatus);
        Optional.ofNullable(request.getFullName()).ifPresent(existedAccount::setFullName);
        Optional.ofNullable(request.getPhoneNumber()).ifPresent(existedAccount::setPhoneNumber);
        Optional.ofNullable(request.getRole()).ifPresent(existedAccount::setRole);
        Optional.ofNullable(request.getUserType()).ifPresent(existedAccount::setUserType);
        return existedAccount;
    }

    @Override
    public AccountResponse toAccountResponse(Account account) {
        return modelMapper.map(account, AccountResponse.class);
    }
}
