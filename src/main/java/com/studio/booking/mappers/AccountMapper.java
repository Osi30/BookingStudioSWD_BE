package com.studio.booking.mappers;

import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;

public interface AccountMapper {
    Account toAccount(AuthRequest authRequest);
    Account updateAccount(AuthRequest request, Account existedAccount);
    Account updateAccount(AccountRequest request, Account existedAccount);
    AccountResponse toAccountResponse(Account account);
}
