package com.studio.booking.repositories;

import com.studio.booking.entities.StudioType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudioTypeRepo extends JpaRepository<StudioType, String> {
    List<StudioType> findAllByIsDeletedFalse();

    @Query("""
            SELECT st.bufferTime
            FROM StudioType st
            WHERE st.isDeleted=false
            AND st.id = :typeId
            """)
    Long getStudioBufferMinutes(String typeId);
}
