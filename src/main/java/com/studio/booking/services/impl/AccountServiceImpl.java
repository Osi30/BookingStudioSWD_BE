package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.enums.UserType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AccountMapper;
import com.studio.booking.repositories.AccountRepo;
import com.studio.booking.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;

    @Override
    public Account createAccount(String email, String name) {
        // Create new account
        return accountRepo.save(Account.builder()
                .email(email)
                .fullName(name)
                .userType(UserType.PERSONAL)
                .role(AccountRole.CUSTOMER)
                .status(AccountStatus.ACTIVE)
                .build());
    }

    @Override
    public Account getAccountById(String accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found with id: " + accountId));
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepo.findByEmail(email);
    }

    @Override
    public AccountResponse updateAccount(AccountRequest request, String accountId) {
        Account existingAccount = getAccountById(accountId);
        existingAccount = accountMapper.updateAccount(request, existingAccount);
        return accountMapper.toAccountResponse(accountRepo.save(existingAccount));
    }

    @Override
    public String deleteAccount(String accountId) {
        Account account = getAccountById(accountId);

        // Set status
        account.setStatus(AccountStatus.DELETED);

        // Set unique fields
        String prefix = "deleted-" + UUID.randomUUID() + "-";
        account.setEmail(prefix + account.getEmail());

        accountRepo.save(account);

        return "Delete Account Successfully";
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
    public String unban(String accountId) {
        Account acc = accountRepo.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found with id: " + accountId));
        acc.setStatus(AccountStatus.ACTIVE);
        accountRepo.save(acc);
        return "Unbanned account with id: " + accountId;
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        return accountRepo.findAllByStatusIsIn(List.of(AccountStatus.ACTIVE, AccountStatus.BANNED)).stream()
                .map(accountMapper::toAccountResponse)
                .toList();
    }
}
