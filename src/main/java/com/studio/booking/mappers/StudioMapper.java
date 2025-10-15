package com.studio.booking.mappers;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.Studio;

public interface StudioMapper {
    Studio toEntity(StudioRequest req);
    Studio updateEntity(Studio existing, StudioRequest req);
    StudioResponse toResponse(Studio studio);
}
