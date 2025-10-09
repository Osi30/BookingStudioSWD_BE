package com.studio.booking.repositories;

import com.studio.booking.entities.PriceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRuleRepo extends JpaRepository<PriceRule, String> {
    List<PriceRule> findAllByPriceTableItem_IdAndIsDeletedFalse(String itemId);
}
