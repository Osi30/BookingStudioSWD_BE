package com.studio.booking.services;

import com.studio.booking.dtos.request.LocationRequest;
import com.studio.booking.entities.Location;

import java.util.List;

public interface LocationService {
    List<Location> getAll();
    Location getById(String id);
    Location create(LocationRequest req);
    Location update(String id, LocationRequest req);
    String delete(String id);
    String restore(String id);
}
