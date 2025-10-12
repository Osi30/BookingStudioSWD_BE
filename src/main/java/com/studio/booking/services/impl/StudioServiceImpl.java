package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.Location;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioType;
import com.studio.booking.enums.StudioStatus;
import com.studio.booking.exceptions.exceptions.StudioException;
import com.studio.booking.mappers.StudioMapper;
import com.studio.booking.repositories.LocationRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {
    private final StudioRepo studioRepo;
    private final LocationRepo locationRepo;
    private final StudioTypeRepo studioTypeRepo;
    private final StudioMapper mapper;

    @Override
    public List<StudioResponse> getAll() {
        return studioRepo.findAllByStatusNot(StudioStatus.DELETED).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StudioResponse getById(String id) {
        Studio studio = studioRepo.findById(id)
                .orElseThrow(() -> new StudioException("Studio not found with id: " + id));
        return mapper.toResponse(studio);
    }

    @Override
    public StudioResponse create(StudioRequest req) {
        Location location = locationRepo.findById(req.getLocationId())
                .orElseThrow(() -> new StudioException("Location not found with id: " + req.getLocationId()));
        StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                .orElseThrow(() -> new StudioException("Studio type not found with id: " + req.getStudioTypeId()));

        if (req.getArea() > type.getMaxArea() || req.getArea() < type.getMinArea()){
            throw new StudioException("Area out of range");
        }

        Studio studio = mapper.toEntity(req);
        studio.setLocation(location);
        studio.setStudioType(type);
        studio.setStatus(StudioStatus.AVAILABLE);

        return mapper.toResponse(studioRepo.save(studio));
    }

    @Override
    public StudioResponse update(String id, StudioRequest req) {
        Studio existing = studioRepo.findById(id)
                .orElseThrow(() -> new StudioException("Studio not found: " + id));
        existing = mapper.updateEntity(existing, req);

        if (req.getLocationId() != null) {
            Location location = locationRepo.findById(req.getLocationId())
                    .orElseThrow(() -> new StudioException("Location not found: " + req.getLocationId()));
            existing.setLocation(location);
        }

        if (req.getStudioTypeId() != null) {
            StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                    .orElseThrow(() -> new StudioException("Studio type not found: " + req.getStudioTypeId()));
            existing.setStudioType(type);
        }

        if (req.getArea() > existing.getStudioType().getMaxArea() || req.getArea() < existing.getStudioType().getMinArea()){
            throw new StudioException("Area out of range");
        }

        return mapper.toResponse(studioRepo.save(existing));
    }

    @Override
    public String delete(String id) {
        Studio existing = studioRepo.findById(id)
                .orElseThrow(() -> new StudioException("Studio not found: " + id));
        existing.setStatus(StudioStatus.DELETED);
        studioRepo.save(existing);
        return "Studio deleted successfully!";
    }

    @Override
    public String restore(String id) {
        Studio existing = studioRepo.findById(id)
                .orElseThrow(() -> new StudioException("Studio not found: " + id));
        existing.setStatus(StudioStatus.AVAILABLE);
        studioRepo.save(existing);
        return "Studio restored successfully!";
    }
}
