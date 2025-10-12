package com.studio.booking.repositories;

import com.studio.booking.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepo  extends JpaRepository<Location, String> {
    List<Location> findAllByIsDeletedFalse();
}
