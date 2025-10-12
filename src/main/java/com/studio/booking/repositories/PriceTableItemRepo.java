package com.studio.booking.repositories;

import com.studio.booking.entities.PriceTableItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceTableItemRepo extends JpaRepository<PriceTableItem, String> {
    List<PriceTableItem> findAllByPriceTable_Id(String priceTableId);
}
