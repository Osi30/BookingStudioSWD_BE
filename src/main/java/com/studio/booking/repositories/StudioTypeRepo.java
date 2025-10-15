package com.studio.booking.repositories;

import com.studio.booking.entities.StudioType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudioTypeRepo extends JpaRepository<StudioType, String> {
    List<StudioType> findAllByIsDeletedFalse();
}
