package com.studio.booking.services;

import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;

import java.util.List;

public interface AccountService {
    Account createAccount(String email, String name);

    Account getAccountById(String accountId);

    Account getAccountByEmail(String email);

    AccountResponse updateAccount(AccountRequest account, String accountId);

    String deleteAccount(String accountId);

    String banAccount(String accountId);

    List<AccountResponse> getAllAccounts();
}
