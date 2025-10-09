package com.studio.booking.repositories;

import com.studio.booking.entities.Studio;
import com.studio.booking.enums.StudioStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudioRepo extends JpaRepository<Studio, String> {
    List<Studio> findAllByStatusNot(StudioStatus status);
}
