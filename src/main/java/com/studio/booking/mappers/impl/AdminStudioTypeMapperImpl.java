package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AdminStudioTypeRequest;
import com.studio.booking.entities.StudioType;
import com.studio.booking.mappers.AdminStudioTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminStudioTypeMapperImpl implements AdminStudioTypeMapper {
    @Override
    public StudioType toEntity(AdminStudioTypeRequest req) {
        return StudioType.builder()
                .name(req.getName())
                .description(req.getDescription())
                .minArea(req.getMinArea())
                .maxArea(req.getMaxArea())
                .isDeleted(false)
                .build();
    }

    @Override
    public StudioType updateEntity(StudioType existing, AdminStudioTypeRequest req) {
        Optional.ofNullable(req.getName()).ifPresent(existing::setName);
        Optional.ofNullable(req.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(req.getMinArea()).ifPresent(existing::setMinArea);
        Optional.ofNullable(req.getMaxArea()).ifPresent(existing::setMaxArea);
        return existing;
    }
}
