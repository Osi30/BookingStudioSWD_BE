package com.studio.booking.repositories;

import com.studio.booking.entities.PriceTable;
import com.studio.booking.enums.PriceTableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceTableRepo extends JpaRepository<PriceTable, String> {
    List<PriceTable> findAllByStatusNot(PriceTableStatus status);
}

