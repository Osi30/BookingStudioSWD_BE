package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminPriceTableRequest;
import com.studio.booking.dtos.response.AdminPriceTableResponse;

import java.util.List;

public interface AdminPriceTableService {
    List<AdminPriceTableResponse> getAll();
    AdminPriceTableResponse getById(String id);
    AdminPriceTableResponse create(AdminPriceTableRequest req);
    AdminPriceTableResponse update(String id, AdminPriceTableRequest req);
    String delete(String id);
}
