package com.studio.booking.mappers;

import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;

public interface AccountMapper {
    AccountResponse toAccountResponse(Account account);
}
