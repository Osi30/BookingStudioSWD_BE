package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.LocationRequest;
import com.studio.booking.entities.Location;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.LocationMapper;
import com.studio.booking.repositories.LocationRepo;
import com.studio.booking.services.LocationService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepo repo;
    private final LocationMapper mapper;

    @Override
    public List<Location> getAll(String typeId) {
        if (Validation.isNullOrEmpty(typeId)) {
            return repo.findAllByIsDeletedFalse();
        }
        return repo.findAllByStudioType(typeId);
    }

    @Override
    public Location getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new AccountException("Location not found: " + id));
    }

    @Override
    public Location create(LocationRequest req) {
        return repo.save(mapper.toEntity(req));
    }

    @Override
    public Location update(String id, LocationRequest req) {
        Location existing = getById(id);
        existing = mapper.updateEntity(existing, req);
        return repo.save(existing);
    }

    @Override
    public String delete(String id) {
        Location existing = getById(id);
        existing.setIsDeleted(true);
        repo.save(existing);
        return "Location deleted successfully!";
    }

    @Override
    public String restore(String id) {
        Location existing = getById(id);
        existing.setIsDeleted(false);
        repo.save(existing);
        return "Location restored successfully!";
    }
}
