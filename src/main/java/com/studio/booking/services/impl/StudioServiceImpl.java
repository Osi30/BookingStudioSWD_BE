package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.StudioRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.Account;
import com.studio.booking.entities.Location;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioType;
import com.studio.booking.enums.AccountRole;
import com.studio.booking.enums.StudioStatus;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.exceptions.exceptions.StudioException;
import com.studio.booking.mappers.StudioMapper;
import com.studio.booking.repositories.LocationRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.repositories.StudioTypeRepo;
import com.studio.booking.services.AccountService;
import com.studio.booking.services.CloudinaryService;
import com.studio.booking.services.StudioService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {
    private final CloudinaryService cloudinaryService;
    private final StudioRepo studioRepo;
    private final LocationRepo locationRepo;
    private final AccountService accountService;
    private final StudioTypeRepo studioTypeRepo;
    private final StudioMapper mapper;

    @Override
    public List<StudioResponse> getAll(String studioTypeId) {
        List<Studio> studios = Validation.isNullOrEmpty(studioTypeId)
                ? studioRepo.findAllByStatusNot(StudioStatus.DELETED)
                : studioRepo.findAllByStatusNotAndStudioTypeId(StudioStatus.DELETED, studioTypeId);

        return studios.stream()
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
    public StudioResponse create(StudioRequest req) throws IOException {
        Location location = locationRepo.findById(req.getLocationId())
                .orElseThrow(() -> new StudioException("Location not found with id: " + req.getLocationId()));
        StudioType type = studioTypeRepo.findById(req.getStudioTypeId())
                .orElseThrow(() -> new StudioException("Studio type not found with id: " + req.getStudioTypeId()));

        if (req.getAcreage() > type.getMaxArea() || req.getAcreage() < type.getMinArea()) {
            throw new StudioException("Area out of range");
        }

        Studio studio = mapper.toEntity(req);
        studio.setLocation(location);
        studio.setStudioType(type);
        studio.setStatus(StudioStatus.AVAILABLE);

        // Image
        String imageUrl = cloudinaryService.uploadImage(req.getImage());
        studio.setImageUrl(imageUrl);

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

        if (req.getAcreage() > existing.getStudioType().getMaxArea() || req.getAcreage() < existing.getStudioType().getMinArea()) {
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

    @Override
    public List<StudioResponse> getForStaff(String staffAccountId) {
        Account staff = accountService.getAccountById(staffAccountId);
        if (staff.getRole() != AccountRole.STAFF) {
            throw new BookingException("Only staff can view bookings by location");
        }
        if (staff.getLocation() == null) {
            throw new BookingException("Staff does not have a location assigned");
        }

        List<Studio> studios = studioRepo.findAllByLocationId(staff.getLocation().getId());
        return studios.stream().map(mapper::toResponse).toList();
    }

    @Override
    public StudioResponse updateStatus(String id, UpdateStatusRequest request) {
        var studio = studioRepo.findById(id)
                .orElseThrow(() -> new BookingException("Studio not found with id: " + id));

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new BookingException("Studio status cannot be null/blank");
        }

        StudioStatus newStatus;
        try {
            newStatus = StudioStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BookingException("Invalid StudioStatus: " + request.getStatus());
        }

        studio.setStatus(newStatus);
        studioRepo.save(studio);

        return mapper.toResponse(studio);
    }

}
