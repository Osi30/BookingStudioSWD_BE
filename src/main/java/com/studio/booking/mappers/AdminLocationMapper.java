package com.studio.booking.mappers;

import com.studio.booking.dtos.request.AdminLocationRequest;
import com.studio.booking.entities.Location;

public interface AdminLocationMapper {
    Location toEntity(AdminLocationRequest req);
    Location updateEntity(Location existing, AdminLocationRequest req);
}
