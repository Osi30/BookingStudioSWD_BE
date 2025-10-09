package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AdminStudioRequest;
import com.studio.booking.dtos.response.AdminStudioResponse;
import com.studio.booking.entities.Location;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioType;
import com.studio.booking.enums.StudioStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AdminStudioMapper;
import com.studio.booking.repositories.LocationRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.AdminStudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStudioServiceImpl implements AdminStudioService {
    private final StudioRepo studioRepo;
    private final LocationRepo locationRepo;
    private final StudioTypeRepo studioTypeRepo;
    private final AdminStudioMapper mapper;

    @Override
    public List<AdminStudioResponse> getAll() {
        return studioRepo.findAllByStatusNot(StudioStatus.DELETED).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AdminStudioResponse getById(String id) {
        Studio studio = studioRepo.findById(id)
                .orElseThrow(() -> new AccountException("Studio not found with id: " + id));
        return mapper.toResponse(studio);
    }

    @Override
    public AdminStudioResponse create(AdminStudioRequest req) {
        Location location = locationRepo.findById(req.getLocationId())
                .orElseThrow(() -> new AccountException("Location not found with id: " + req.getLocationId()));
        StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                .orElseThrow(() -> new AccountException("Studio type not found with id: " + req.getStudioTypeId()));

        Studio studio = mapper.toEntity(req);
        studio.setLocation(location);
        studio.setStudioType(type);
        studio.setStatus(StudioStatus.AVAILABLE);

        return mapper.toResponse(studioRepo.save(studio));
    }

    @Override
    public AdminStudioResponse update(String id, AdminStudioRequest req) {
        Studio existing = studioRepo.findById(id)
                .orElseThrow(() -> new AccountException("Studio not found: " + id));
        existing = mapper.updateEntity(existing, req);

        if (req.getLocationId() != null) {
            Location location = locationRepo.findById(req.getLocationId())
                    .orElseThrow(() -> new AccountException("Location not found: " + req.getLocationId()));
            existing.setLocation(location);
        }

        if (req.getStudioTypeId() != null) {
            StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                    .orElseThrow(() -> new AccountException("Studio type not found: " + req.getStudioTypeId()));
            existing.setStudioType(type);
        }

        return mapper.toResponse(studioRepo.save(existing));
    }

    @Override
    public String delete(String id) {
        Studio existing = studioRepo.findById(id)
                .orElseThrow(() -> new AccountException("Studio not found: " + id));
        existing.setStatus(StudioStatus.DELETED);
        studioRepo.save(existing);
        return "Studio deleted successfully!";
    }

    @Override
    public String restore(String id) {
        Studio existing = studioRepo.findById(id)
                .orElseThrow(() -> new AccountException("Studio not found: " + id));
        existing.setStatus(StudioStatus.AVAILABLE);
        studioRepo.save(existing);
        return "Studio restored successfully!";
    }
}
