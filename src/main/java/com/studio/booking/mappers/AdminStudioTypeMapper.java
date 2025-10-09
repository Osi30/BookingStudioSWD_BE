package com.studio.booking.mappers;

import com.studio.booking.dtos.request.AdminStudioTypeRequest;
import com.studio.booking.entities.StudioType;

public interface AdminStudioTypeMapper {
    StudioType toEntity(AdminStudioTypeRequest req);
    StudioType updateEntity(StudioType existing, AdminStudioTypeRequest req);
}
