package com.studio.booking.repositories;

import com.studio.booking.entities.FcmToken;
import com.studio.booking.enums.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepo extends JpaRepository<FcmToken, String> {
    Optional<FcmToken> findByToken(String token);

    @Query("""
            SELECT t
            FROM FcmToken t
            JOIN Account a ON t.account.id = a.id
            JOIN Location l ON a.location.id = l.id
            WHERE t.account.role = :role
            AND a.status = com.studio.booking.enums.AccountStatus.ACTIVE
            AND l.id = :locationId
            """)
    List<FcmToken> findAllByRoleStaffAndLocation(AccountRole role, String locationId);

    @Modifying
    @Transactional
    void deleteAllByTokenIsIn(List<String> tokens);
}
