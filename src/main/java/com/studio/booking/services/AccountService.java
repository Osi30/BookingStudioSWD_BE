package com.studio.booking.services;

import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;

import java.util.List;

public interface AccountService {
    Account getAccountById(String accountId);

    AccountResponse getAccountResponseById(String accountId);

    String banAccount(String accountId);

    List<Account> getAllAccounts();

    List<AccountResponse> getAllAccountResponses();
}
