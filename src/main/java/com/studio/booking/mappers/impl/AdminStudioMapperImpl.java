package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AdminStudioRequest;
import com.studio.booking.dtos.response.AdminStudioResponse;
import com.studio.booking.entities.Studio;
import com.studio.booking.mappers.AdminStudioMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminStudioMapperImpl  implements AdminStudioMapper {
    @Override
    public Studio toEntity(AdminStudioRequest req) {
        return Studio.builder()
                .studioName(req.getStudioName())
                .description(req.getDescription())
                .area(req.getArea())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus())
                .build();
    }

    @Override
    public Studio updateEntity(Studio existing, AdminStudioRequest req) {
        Optional.ofNullable(req.getStudioName()).ifPresent(existing::setStudioName);
        Optional.ofNullable(req.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(req.getArea()).ifPresent(existing::setArea);
        Optional.ofNullable(req.getStartTime()).ifPresent(existing::setStartTime);
        Optional.ofNullable(req.getEndTime()).ifPresent(existing::setEndTime);
        Optional.ofNullable(req.getStatus()).ifPresent(existing::setStatus);
        return existing;
    }

    @Override
    public AdminStudioResponse toResponse(Studio studio) {
        return AdminStudioResponse.builder()
                .id(studio.getId())
                .studioName(studio.getStudioName())
                .description(studio.getDescription())
                .area(studio.getArea())
                .startTime(studio.getStartTime())
                .endTime(studio.getEndTime())
                .status(studio.getStatus())
                .locationName(studio.getLocation() != null ? studio.getLocation().getLocationName() : null)
                .studioTypeName(studio.getStudioType() != null ? studio.getStudioType().getName() : null)
                .build();
    }
}
