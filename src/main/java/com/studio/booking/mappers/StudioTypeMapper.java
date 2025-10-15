package com.studio.booking.mappers;

import com.studio.booking.dtos.request.StudioTypeRequest;
import com.studio.booking.entities.StudioType;

public interface StudioTypeMapper {
    StudioType toEntity(StudioTypeRequest req);
    StudioType updateEntity(StudioType existing, StudioTypeRequest req);
}
