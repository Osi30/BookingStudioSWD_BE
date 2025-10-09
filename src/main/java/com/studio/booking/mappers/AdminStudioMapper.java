package com.studio.booking.mappers;

import com.studio.booking.dtos.request.AdminStudioRequest;
import com.studio.booking.dtos.response.AdminStudioResponse;
import com.studio.booking.entities.Studio;

public interface AdminStudioMapper {
    Studio toEntity(AdminStudioRequest req);
    Studio updateEntity(Studio existing, AdminStudioRequest req);
    AdminStudioResponse toResponse(Studio studio);
}
