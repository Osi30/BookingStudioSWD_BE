package com.studio.booking.services.impl;

import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AccountMapper;
import com.studio.booking.repositories.AccountRepo;
import com.studio.booking.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;

    @Override
    public Account getAccountById(String accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found with id: " + accountId));
    }

    @Override
    public AccountResponse getAccountResponseById(String accountId) {
        return accountMapper.toAccountResponse(getAccountById(accountId));
    }

    @Override
    public String banAccount(String accountId) {
        Account account = getAccountById(accountId);

        if (account.getRole().equals(AccountRole.ADMIN)) {
            throw new AccountException("Admin account cannot be banned.");
        }

        account.setStatus(AccountStatus.BANNED);
        accountRepo.save(account);

        return "Banned Account with id: " + accountId;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepo.findAllByStatusIsIn(List.of(AccountStatus.ACTIVE, AccountStatus.BANNED));
    }

    @Override
    public List<AccountResponse> getAllAccountResponses() {
        return getAllAccounts().stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }
}
