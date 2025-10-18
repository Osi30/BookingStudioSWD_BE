package com.studio.booking.repositories;

import com.studio.booking.entities.PriceTable;
import com.studio.booking.enums.PriceTableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceTableRepo extends JpaRepository<PriceTable, String> {
    List<PriceTable> findAllByStatusNot(PriceTableStatus status);

    @Query("""
            SELECT pt
            FROM PriceTable pt
            JOIN PriceTableItem pi ON pt.id = pi.priceTable.id
            JOIN StudioType st ON st.id = pi.studioType.id
            WHERE st.id = :studioTypeId
            AND pt.status = com.studio.booking.enums.PriceTableStatus.COMING_SOON
            OR pt.status = com.studio.booking.enums.PriceTableStatus.IS_HAPPENING
            """)
    List<PriceTable> findAllByStudioType(String studioTypeId);
}

