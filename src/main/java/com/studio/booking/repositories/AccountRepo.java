package com.studio.booking.repositories;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AccountRepo extends JpaRepository<Account, String> {
    Account findByEmail(String email);

    Account findByUsername(String username);

    List<Account> findAllByStatusIsIn(Collection<AccountStatus> status);
}
