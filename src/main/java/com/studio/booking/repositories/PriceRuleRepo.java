package com.studio.booking.repositories;

import com.studio.booking.entities.PriceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceRuleRepo extends JpaRepository<PriceRule, String> {
    List<PriceRule> findAllByPriceTableItem_IdAndIsDeletedFalse(String itemId);

    @Query("""
            SELECT pr
            FROM PriceRule pr
            JOIN PriceTableItem pti ON pti.id = pr.priceTableItem.id
            AND pti.studioType.id = :studioTypeId
            AND pti.priceTable.id = :tableId
            """)
    List<PriceRule> findAllByTableAndStudioType(String tableId, String studioTypeId);
}
