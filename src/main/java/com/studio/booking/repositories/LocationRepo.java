package com.studio.booking.repositories;

import com.studio.booking.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepo  extends JpaRepository<Location, String> {
    List<Location> findAllByIsDeletedFalse();

    @Query("""
            SELECT DISTINCT s.location
            FROM Studio s
            WHERE s.studioType.id = :typeId
            AND s.location.isDeleted = false
            """)
    List<Location> findAllByStudioType(String typeId);
}
