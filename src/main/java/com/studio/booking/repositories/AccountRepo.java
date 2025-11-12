package com.studio.booking.repositories;

import com.studio.booking.entities.Account;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface AccountRepo extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
    Account findByEmail(String email);

    int countByRoleIs(AccountRole accountRole);

    List<Account> findAllByStatusIsIn(Collection<AccountStatus> statuses);
}
