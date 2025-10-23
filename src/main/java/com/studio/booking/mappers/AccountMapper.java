package com.studio.booking.mappers;

import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;

public interface AccountMapper {
    Account toAccount(AccountRequest accountRequest);
    Account updateAccount(AccountRequest request, Account existedAccount);
    AccountResponse toAccountResponse(Account account);
}
