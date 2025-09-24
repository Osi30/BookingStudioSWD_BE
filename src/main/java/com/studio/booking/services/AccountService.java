package com.studio.booking.services;

import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;

import java.util.List;

public interface AccountService {
    Account createAccount(AuthRequest authRequest);

    Account getAccountById(String accountId);

    AccountResponse getAccountResponseById(String accountId);

    String banAccount(String accountId);

    List<Account> getAllAccounts();

    List<AccountResponse> getAllAccountResponses();
}
