package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.StudioTypeRequest;
import com.studio.booking.entities.StudioType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.StudioTypeException;
import com.studio.booking.mappers.StudioTypeMapper;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.StudioTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudioTypeServiceImpl implements StudioTypeService {
    private final StudioTypeRepo repo;
    private final ServiceRepo serviceRepo;
    private final StudioTypeMapper mapper;

    @Override
    @Cacheable(value = "studioTypes", key = "'AllStudioTypes'")
    public List<StudioType> getAll() {
        return repo.findAllByIsDeletedFalse();
    }

    @Override
    public StudioType getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new AccountException("Studio type not found: " + id));
    }

    @Override
    @CacheEvict(value = {"studioTypes"}, allEntries = true)
    public StudioType create(StudioTypeRequest req) {
        validateArea(req);
        StudioType type = mapper.toEntity(req);
        if (req.getServiceIds() != null && !req.getServiceIds().isEmpty()) {
            List<com.studio.booking.entities.Service> services = serviceRepo.findAllById(req.getServiceIds());
            type.setServices(services);
        }
        return repo.save(type);
    }

    @Override
    @CacheEvict(value = {"studioTypes"}, allEntries = true)
    public StudioType update(String id, StudioTypeRequest req) {
        validateArea(req);
        StudioType existing = getById(id);
        existing = mapper.updateEntity(existing, req);
        if (req.getServiceIds() != null) {
            existing.setServices(serviceRepo.findAllById(req.getServiceIds()));
        }
        return repo.save(existing);
    }

    @Override
    @CacheEvict(value = {"studioTypes"}, allEntries = true)
    public String delete(String id) {
        StudioType existing = getById(id);
        existing.setIsDeleted(true);
        repo.save(existing);
        return "Studio type deleted successfully!";
    }

    @Override
    @CacheEvict(value = {"studioTypes"}, allEntries = true)
    public String restore(String id) {
        StudioType existing = getById(id);
        existing.setIsDeleted(false);
        repo.save(existing);
        return "Studio type restored successfully!";
    }

    private void validateArea(StudioTypeRequest req) {
        if (req.getMinArea() < 0 || req.getMaxArea() < 0) {
            throw new StudioTypeException("Min and Max Area cannot be less than zero!");
        }

        if (req.getMinArea() > req.getMaxArea()) {
            throw new StudioTypeException("Min Area cannot be greater than Max Area!");
        }
    }
}
