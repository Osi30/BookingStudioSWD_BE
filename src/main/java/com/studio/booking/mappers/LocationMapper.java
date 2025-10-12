package com.studio.booking.mappers;

import com.studio.booking.dtos.request.LocationRequest;
import com.studio.booking.entities.Location;

public interface LocationMapper {
    Location toEntity(LocationRequest req);
    Location updateEntity(Location existing, LocationRequest req);
}
