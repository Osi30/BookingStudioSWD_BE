package com.studio.booking.services;

import com.studio.booking.dtos.request.PriceTableItemRequest;
import com.studio.booking.dtos.response.PriceTableItemResponse;

import java.util.List;

public interface PriceTableItemService {
    List<PriceTableItemResponse> getByTableId(String priceTableId);
    PriceTableItemResponse create(PriceTableItemRequest req);
    PriceTableItemResponse update(String id, PriceTableItemRequest req);
    String delete(String id);
}
