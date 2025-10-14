package com.studio.booking.repositories;

import com.studio.booking.entities.Studio;
import com.studio.booking.enums.StudioStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface StudioRepo extends JpaRepository<Studio, String> {
    List<Studio> findAllByStatusNot(StudioStatus status);
    List<Studio> findAllByStatusNotAndStudioTypeId(StudioStatus status, String studioTypeId);

    @Query("""
            SELECT s
            FROM Studio s
            JOIN Location l ON l.id = s.location.id
            JOIN StudioType st ON st.id = s.studioType.id
            WHERE l.id = :locationId
            AND st.id = :typeId
            AND s.status = com.studio.booking.enums.StudioStatus.AVAILABLE
            """)
    List<Studio> findAvailableStudio(String locationId, String typeId);

    @Query("""
            SELECT DISTINCT sa.studio.id
            FROM StudioAssign sa
            JOIN Studio s ON s.id = sa.studio.id
            WHERE s.location.id = :locationId
            AND s.studioType = :typeId
            AND s.status = com.studio.booking.enums.StudioStatus.AVAILABLE
            AND sa.startTime < :end
            AND ADD_MINUTES(sa.endTime, s.studioType.bufferTime)  > :start
            """)
    Set<String> findOccupiedStudioIds(String locationId, String typeId, LocalTime start, LocalTime end);
}
