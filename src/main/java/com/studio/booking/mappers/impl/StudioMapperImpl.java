package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.Studio;
import com.studio.booking.mappers.StudioMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StudioMapperImpl implements StudioMapper {
    @Override
    public Studio toEntity(StudioRequest req) {
        return Studio.builder()
                .studioName(req.getStudioName())
                .description(req.getDescription())
                .acreage(req.getAcreage())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus())
                .build();
    }

    @Override
    public Studio updateEntity(Studio existing, StudioRequest req) {
        Optional.ofNullable(req.getStudioName()).ifPresent(existing::setStudioName);
        Optional.ofNullable(req.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(req.getAcreage()).ifPresent(existing::setAcreage);
        Optional.ofNullable(req.getStartTime()).ifPresent(existing::setStartTime);
        Optional.ofNullable(req.getEndTime()).ifPresent(existing::setEndTime);
        Optional.ofNullable(req.getStatus()).ifPresent(existing::setStatus);
        return existing;
    }

    @Override
    public StudioResponse toResponse(Studio studio) {
        return StudioResponse.builder()
                .id(studio.getId())
                .studioName(studio.getStudioName())
                .description(studio.getDescription())
                .acreage(studio.getAcreage())
                .startTime(studio.getStartTime())
                .endTime(studio.getEndTime())
                .imageUrl(studio.getImageUrl())
                .status(studio.getStatus())
                .locationName(studio.getLocation() != null ? studio.getLocation().getLocationName() : null)
                .studioTypeName(studio.getStudioType() != null ? studio.getStudioType().getName() : null)
                .build();
    }
}
