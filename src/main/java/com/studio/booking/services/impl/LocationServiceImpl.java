package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.LocationRequest;
import com.studio.booking.entities.Location;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.LocationMapper;
import com.studio.booking.repositories.LocationRepo;
import com.studio.booking.services.LocationService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepo repo;
    private final LocationMapper mapper;

    @Override
    @Cacheable(value = "locations", key = "'AllLocations'")
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
    @CacheEvict(value = {"locations"}, allEntries = true)
    public Location create(LocationRequest req) {
        return repo.save(mapper.toEntity(req));
    }

    @Override
    @CacheEvict(value = {"locations"}, allEntries = true)
    public Location update(String id, LocationRequest req) {
        Location existing = getById(id);
        existing = mapper.updateEntity(existing, req);
        return repo.save(existing);
    }

    @Override
    @CacheEvict(value = {"locations"}, allEntries = true)
    public String delete(String id) {
        Location existing = getById(id);
        existing.setIsDeleted(true);
        repo.save(existing);
        return "Location deleted successfully!";
    }

    @Override
    @CacheEvict(value = {"locations"}, allEntries = true)
    public String restore(String id) {
        Location existing = getById(id);
        existing.setIsDeleted(false);
        repo.save(existing);
        return "Location restored successfully!";
    }
}
