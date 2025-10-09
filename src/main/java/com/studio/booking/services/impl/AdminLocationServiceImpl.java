package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AdminLocationRequest;
import com.studio.booking.entities.Location;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AdminLocationMapper;
import com.studio.booking.repositories.LocationRepo;
import com.studio.booking.services.AdminLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminLocationServiceImpl implements AdminLocationService {
    private final LocationRepo repo;
    private final AdminLocationMapper mapper;

    @Override
    public List<Location> getAll() {
        return repo.findAllByIsDeletedFalse();
    }

    @Override
    public Location getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new AccountException("Location not found: " + id));
    }

    @Override
    public Location create(AdminLocationRequest req) {
        return repo.save(mapper.toEntity(req));
    }

    @Override
    public Location update(String id, AdminLocationRequest req) {
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
