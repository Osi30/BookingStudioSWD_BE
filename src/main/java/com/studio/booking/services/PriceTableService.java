package com.studio.booking.services;

import com.studio.booking.dtos.request.PriceTableRequest;
import com.studio.booking.dtos.response.PriceTableResponse;

import java.util.List;

public interface PriceTableService {
    List<PriceTableResponse> getAll();
    List<PriceTableResponse> getByTypeId(String typeId);
    PriceTableResponse getById(String id);
    PriceTableResponse create(PriceTableRequest req);
    PriceTableResponse update(String id, PriceTableRequest req);
    String delete(String id);
}
