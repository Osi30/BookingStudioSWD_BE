package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminPriceRuleRequest;
import com.studio.booking.dtos.response.AdminPriceRuleResponse;

import java.util.List;

public interface AdminPriceRuleService {
    List<AdminPriceRuleResponse> getByItemId(String priceTableItemId);
    AdminPriceRuleResponse create(AdminPriceRuleRequest req);
    AdminPriceRuleResponse update(String id, AdminPriceRuleRequest req);
    String delete(String id);
}
