package com.studio.booking.repositories;

import com.studio.booking.entities.PriceTableItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PriceTableItemRepo extends JpaRepository<PriceTableItem, String> {
    List<PriceTableItem> findAllByPriceTable_Id(String priceTableId);

    @Query("""
            SELECT pct
            FROM PriceTableItem pct
            JOIN PriceTable p ON pct.priceTable.id = p.id
            JOIN StudioType st ON pct.studioType.id = st.id
            WHERE :date >= p.startDate
            AND :date <= p.endDate
            AND st.id = :studioTypeId
            AND (p.status = com.studio.booking.enums.PriceTableStatus.IS_HAPPENING
            OR p.status = com.studio.booking.enums.PriceTableStatus.COMING_SOON)
            ORDER BY p.priority ASC
            """)
    List<PriceTableItem> findFirstByStudioTypeAndDate(String studioTypeId, LocalDate date);
}
