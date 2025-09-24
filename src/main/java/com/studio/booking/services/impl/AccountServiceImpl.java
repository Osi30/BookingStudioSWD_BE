package com.studio.booking.services.impl;

import com.studio.booking.dtos.dto.AccountIdentity;
import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountIdentifier;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.AuthException;
import com.studio.booking.mappers.AccountMapper;
import com.studio.booking.repositories.AccountRepo;
import com.studio.booking.services.AccountService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;

    @Override
    public Account createAccount(AuthRequest authRequest) {
        // Validation
        AccountIdentity accountIdentity = validateRequestedAuth(authRequest);
        Account existedAccount = accountIdentity.getProcessAccount();

        // Throw exception for active/banned/deleted account
        if (existedAccount != null && !accountIdentity.getIsInactive()) {
            throw new AuthException("Account already exists with: "
                    + accountIdentity.getIdentifier().getValue()
                    + ", status: " + existedAccount.getStatus().getCode());
        }

        // Update new info to inactive account
        if (existedAccount != null) {
            existedAccount = accountMapper.updateAccount(authRequest, existedAccount);
            return accountRepo.save(existedAccount);
        }

        // Create new account
        Account account = accountMapper.toAccount(authRequest);

        // Set role
        account.setRole(authRequest.getAccountRole() != null
                ? authRequest.getAccountRole() : AccountRole.CUSTOMER);

        return accountRepo.save(account);
    }

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

    private AccountIdentity validateRequestedAuth(AuthRequest authRequest) {
        // Validation
        AccountIdentity identity = new AccountIdentity();

        // Username
        identity.setIdentity(authRequest.getUsername());
        validateIdentity(identity, AccountIdentifier.USERNAME);

        // Email
        identity.setIdentity(authRequest.getPhoneNumber());
        validateIdentity(identity, AccountIdentifier.EMAIL);

        return identity;
    }

    private void validateIdentity(AccountIdentity identity, AccountIdentifier identifier) {
        String identityValue = identity.getIdentity();

        if (Validation.isNullOrEmpty(identityValue)) {
            return;
        }

        Account account = switch (identifier) {
            case EMAIL -> accountRepo.findByEmail(identityValue);
            default -> accountRepo.findByUsername(identityValue);
        };

        // No new account found
        if (account == null) {
            return;
        }

        // No found account before
        if (identity.getProcessAccount() == null) {
            identity.setProcessAccount(account);
            identity.setIdentifier(identifier);
            identity.setIsInactive(account.getStatus().equals(AccountStatus.INACTIVE));
            return;
        }

        // Throw Error If Exist Two Different Account
        if (!account.getId().equals(identity.getProcessAccount().getId())) {
            throw new AccountException("Already exist identity: " + identifier.getValue());
        }
    }

}
