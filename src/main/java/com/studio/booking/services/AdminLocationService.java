package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminLocationRequest;
import com.studio.booking.entities.Location;

import java.util.List;

public interface AdminLocationService {
    List<Location> getAll();
    Location getById(String id);
    Location create(AdminLocationRequest req);
    Location update(String id, AdminLocationRequest req);
    String delete(String id);
    String restore(String id);
}
