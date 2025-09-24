package com.studio.booking.services.impl;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountIdentifier;
import com.studio.booking.enums.AccountStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Account account = accountService.getAccountByIdentifier(identifier, AccountIdentifier.ALL);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found with: " + identifier);
        }

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccountException("Account is: " + account.getStatus().getCode());
        }

        return new User(account.getId(), account.getPassword(), account.getAuthorities());
    }
}
