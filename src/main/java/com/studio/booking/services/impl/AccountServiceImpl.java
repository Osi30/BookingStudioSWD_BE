package com.studio.booking.services.impl;

import com.studio.booking.dtos.dto.AccountIdentity;
import com.studio.booking.dtos.request.AccountRequest;
import com.studio.booking.dtos.request.AuthRequest;
import com.studio.booking.dtos.response.AccountResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountIdentifier;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.enums.TokenType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.AuthException;
import com.studio.booking.mappers.AccountMapper;
import com.studio.booking.repositories.AccountRepo;
import com.studio.booking.services.AccountService;
import com.studio.booking.services.VerifyTokenService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final VerifyTokenService verifyTokenService;
    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    private final ModelMapper modelMapper;

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
    public Account getAccountByIdentifier(String identifier, AccountIdentifier identifierType) {
        return switch (identifierType) {
            case EMAIL -> accountRepo.findByEmail(identifier);
            case USERNAME -> accountRepo.findByUsername(identifier);
            default -> accountRepo.findByIdentifier(identifier);
        };
    }

    @Override
    public AccountResponse getAccountResponseById(String accountId) {
        return accountMapper.toAccountResponse(getAccountById(accountId));
    }

    @Override
    public AccountResponse updateAccount(AccountRequest request, String accountId) {
        Account existingAccount = getAccountById(accountId);

        // Validation
        validateUpdatedAuth(request, existingAccount);

        // Map Common Info
        existingAccount = accountMapper.updateAccount(request, existingAccount);

        // Email Verification Step
        if (existingAccount.getEmail() != null && request.getEmail() != null
                && !existingAccount.getEmail().equals(request.getEmail())) {
            existingAccount.setEmail(request.getEmail());
            verifyTokenService.sendToken(existingAccount, TokenType.VERIFY_EMAIL);
        }

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
        account.setUsername(prefix + account.getUsername());

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
        identity.setIdentity(authRequest.getEmail());
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

    private void validateUpdatedAuth(AccountRequest accountRequest, Account existingAccount) {
        // A. Remove Duplication Request
        // 1. Email
        if (existingAccount.getEmail() != null && accountRequest.getEmail() != null
                && existingAccount.getEmail().equals(accountRequest.getEmail())) {
            accountRequest.setEmail(null);
        }

        // 3. Username
        if (existingAccount.getUsername() != null && accountRequest.getUsername() != null
                && existingAccount.getUsername().equals(accountRequest.getUsername())) {
            accountRequest.setUsername(null);
        }

        // B. Find Existing Account
        AccountIdentity accountIdentity = validateRequestedAuth(modelMapper.map(accountRequest, AuthRequest.class));
        if (accountIdentity.getProcessAccount() != null) {
            throw new AccountException("Account already exists with: " + accountIdentity.getIdentifier().getValue());
        }
    }

}
