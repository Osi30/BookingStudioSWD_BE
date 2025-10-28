package com.studio.booking.repositories;

import com.studio.booking.entities.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepo extends JpaRepository<FcmToken, String> {
    Optional<FcmToken> findByToken(String token);

    List<FcmToken> findAllByAccount_Id(String accountId);

    @Modifying
    @Transactional
    int deleteAllByTokenIsIn(List<String> tokens);
}
