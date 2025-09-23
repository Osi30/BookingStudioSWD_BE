package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountMapperImpl implements AccountMapper {
    private final ModelMapper modelMapper;

    @Override
    public AccountResponse toAccountResponse(Account account) {
        return modelMapper.map(account, AccountResponse.class);
    }
}
