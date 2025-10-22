package com.studio.booking.repositories;

import com.studio.booking.entities.Studio;
import com.studio.booking.enums.StudioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface StudioRepo extends JpaRepository<Studio, String> {
    List<Studio> findAllByStatusNot(StudioStatus status);
    Studio findStudioById(String id);
    List<Studio> findAllByStatusNotAndStudioTypeId(StudioStatus status, String studioTypeId);

    @Query("""
            SELECT s
            FROM Studio s
            WHERE s.id NOT IN :occupiedStudios
            AND s.status = com.studio.booking.enums.StudioStatus.AVAILABLE
            """)
    List<Studio> findAvailableStudio(Set<String> occupiedStudios);

    @Query("""
            SELECT DISTINCT sa.studio.id
            FROM StudioAssign sa
            JOIN Studio s ON s.id = sa.studio.id
            WHERE s.location.id = :locationId
            AND s.studioType.id = :typeId
            AND s.status = com.studio.booking.enums.StudioStatus.AVAILABLE
            AND sa.startTime < :end
            AND sa.endTime > :start
            """)
    Set<String> findOccupiedStudioIds(String locationId, String typeId,
                                      LocalDateTime start, LocalDateTime end
    );

    @Query("""
           select s
           from Studio s
           where s.location.id = :locationId
           """)
    List<Studio> findAllByLocationId(@Param("locationId") String locationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Studio s where s.id = :id")
    Optional<Studio> findByIdForUpdate(@Param("id") String id);
}
