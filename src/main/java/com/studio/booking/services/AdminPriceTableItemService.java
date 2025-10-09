package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminPriceTableItemRequest;
import com.studio.booking.dtos.response.AdminPriceTableItemResponse;

import java.util.List;

public interface AdminPriceTableItemService {
    List<AdminPriceTableItemResponse> getByTableId(String priceTableId);
    AdminPriceTableItemResponse create(AdminPriceTableItemRequest req);
    AdminPriceTableItemResponse update(String id, AdminPriceTableItemRequest req);
    String delete(String id);
}
