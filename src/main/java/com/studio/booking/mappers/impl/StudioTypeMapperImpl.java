package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.StudioTypeRequest;
import com.studio.booking.entities.StudioType;
import com.studio.booking.mappers.StudioTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudioTypeMapperImpl implements StudioTypeMapper {
    @Override
    public StudioType toEntity(StudioTypeRequest req) {
        return StudioType.builder()
                .name(req.getName())
                .description(req.getDescription())
                .minArea(req.getMinArea())
                .maxArea(req.getMaxArea())
                .isDeleted(false)
                .build();
    }

    @Override
    public StudioType updateEntity(StudioType existing, StudioTypeRequest req) {
        Optional.ofNullable(req.getName()).ifPresent(existing::setName);
        Optional.ofNullable(req.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(req.getMinArea()).ifPresent(existing::setMinArea);
        Optional.ofNullable(req.getMaxArea()).ifPresent(existing::setMaxArea);
        Optional.ofNullable(req.getBufferTime()).ifPresent(existing::setBufferTime);
        return existing;
    }
}
