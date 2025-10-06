package com.studio.booking.repositories;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface AccountRepo extends JpaRepository<Account, String> {
    Account findByEmail(String email);

    Account findByUsername(String username);

    Account findByRole(AccountRole role);

    @Query("""
            SELECT a
            FROM Account a
            WHERE a.email = :identifier
            or a.username = :identifier
            """)
    Account findByIdentifier(String identifier);

    List<Account> findAllByStatusIsIn(Collection<AccountStatus> status);
}
