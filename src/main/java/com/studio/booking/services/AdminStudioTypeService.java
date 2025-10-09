package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminStudioTypeRequest;
import com.studio.booking.entities.StudioType;

import java.util.List;

public interface AdminStudioTypeService {
    List<StudioType> getAll();
    StudioType getById(String id);
    StudioType create(AdminStudioTypeRequest req);
    StudioType update(String id, AdminStudioTypeRequest req);
    String delete(String id);
    String restore(String id);
}
