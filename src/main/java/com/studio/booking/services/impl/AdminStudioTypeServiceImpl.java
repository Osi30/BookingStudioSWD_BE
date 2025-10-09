package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AdminStudioTypeRequest;
import com.studio.booking.entities.StudioType;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AdminStudioTypeMapper;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.AdminStudioTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStudioTypeServiceImpl implements AdminStudioTypeService {
    private final StudioTypeRepo repo;
    private final ServiceRepo serviceRepo;
    private final AdminStudioTypeMapper mapper;

    @Override
    public List<StudioType> getAll() {
        return repo.findAllByIsDeletedFalse();
    }

    @Override
    public StudioType getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new AccountException("Studio type not found: " + id));
    }

    @Override
    public StudioType create(AdminStudioTypeRequest req) {
        StudioType type = mapper.toEntity(req);
        if (req.getServiceIds() != null && !req.getServiceIds().isEmpty()) {
            List<com.studio.booking.entities.Service> services = serviceRepo.findAllById(req.getServiceIds());
            type.setServices(services);
        }
        return repo.save(type);
    }

    @Override
    public StudioType update(String id, AdminStudioTypeRequest req) {
        StudioType existing = getById(id);
        existing = mapper.updateEntity(existing, req);
        if (req.getServiceIds() != null) {
            existing.setServices(serviceRepo.findAllById(req.getServiceIds()));
        }
        return repo.save(existing);
    }

    @Override
    public String delete(String id) {
        StudioType existing = getById(id);
        existing.setIsDeleted(true);
        repo.save(existing);
        return "Studio type deleted successfully!";
    }

    @Override
    public String restore(String id) {
        StudioType existing = getById(id);
        existing.setIsDeleted(false);
        repo.save(existing);
        return "Studio type restored successfully!";
    }
}
