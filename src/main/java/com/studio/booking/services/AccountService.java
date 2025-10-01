package com.studio.booking.services;

import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountIdentifier;

import java.util.List;

public interface AccountService {
    Account createAccount(AuthRequest authRequest);

    Account getAccountById(String accountId);

    Account getAccountByIdentifier(String identifier, AccountIdentifier identifierType);

    AccountResponse getAccountResponseById(String accountId);

    AccountResponse updateAccount(AccountRequest account, String accountId);

    String deleteAccount(String accountId);

    String banAccount(String accountId);

    List<Account> getAllAccounts();

    List<AccountResponse> getAllAccountResponses();
}
