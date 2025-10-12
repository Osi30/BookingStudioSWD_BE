package com.studio.booking.services;

import com.studio.booking.dtos.request.StudioTypeRequest;
import com.studio.booking.entities.StudioType;

import java.util.List;

public interface StudioTypeService {
    List<StudioType> getAll();
    StudioType getById(String id);
    StudioType create(StudioTypeRequest req);
    StudioType update(String id, StudioTypeRequest req);
    String delete(String id);
    String restore(String id);
}
