package com.studio.booking.repositories;

import com.studio.booking.entities.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyTokenRepo extends JpaRepository<VerifyToken, String> {
    VerifyToken findByToken(String token);
}
