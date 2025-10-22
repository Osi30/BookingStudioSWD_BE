package com.studio.booking.services;

import com.studio.booking.dtos.request.PriceRuleRequest;
import com.studio.booking.dtos.response.PriceRuleResponse;

import java.util.List;

public interface PriceRuleService {
    List<PriceRuleResponse> getByItemId(String priceTableItemId);
    List<PriceRuleResponse> getByTableAndType(String tableId, String typeId);
    PriceRuleResponse create(PriceRuleRequest req);
    PriceRuleResponse update(String id, PriceRuleRequest req);
    String delete(String id);
}
