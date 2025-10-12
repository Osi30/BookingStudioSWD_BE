package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.LocationRequest;
import com.studio.booking.entities.Location;
import com.studio.booking.mappers.LocationMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LocationMapperImpl implements LocationMapper {
    @Override
    public Location toEntity(LocationRequest req) {
        return Location.builder()
                .locationName(req.getLocationName())
                .address(req.getAddress())
                .contactNumber(req.getContactNumber())
                .longitude(req.getLongitude())
                .latitude(req.getLatitude())
                .isDeleted(false)
                .build();
    }

    @Override
    public Location updateEntity(Location existing, LocationRequest req) {
        Optional.ofNullable(req.getLocationName()).ifPresent(existing::setLocationName);
        Optional.ofNullable(req.getAddress()).ifPresent(existing::setAddress);
        Optional.ofNullable(req.getContactNumber()).ifPresent(existing::setContactNumber);
        Optional.ofNullable(req.getLongitude()).ifPresent(existing::setLongitude);
        Optional.ofNullable(req.getLatitude()).ifPresent(existing::setLatitude);
        return existing;
    }
}
