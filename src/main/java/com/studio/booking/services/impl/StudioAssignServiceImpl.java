package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.ServiceAssignRequest;
import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.StudioAssignResponse;
import com.studio.booking.entities.Service;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.repositories.StudioAssignRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.services.ServiceAssignService;
import com.studio.booking.services.StudioAssignService;
import lombok.RequiredArgsConstructor;
import com.studio.booking.exceptions.exceptions.AccountException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class StudioAssignServiceImpl implements StudioAssignService {
    private final ServiceAssignService serviceAssignService;
    private final StudioAssignRepo assignRepo;
    private final StudioRepo studioRepo;

    @Override
    public List<StudioAssignResponse> getAll() {
        return assignRepo.findAllByStatusNot(AssignStatus.CANCELLED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StudioAssignResponse> getByBooking(String bookingId) {
        return assignRepo.findAllByBooking_Id(bookingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StudioAssignResponse> getByStudio(String studioId) {
        return assignRepo.findAllByStudio_Id(studioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public StudioAssign create(StudioAssignRequest req) {
        Set<String> occupiedStudios = studioRepo.findOccupiedStudioIds(
                req.getLocationId(), req.getStudioTypeId(),
                req.getStartTime().toLocalTime(), req.getEndTime().toLocalTime()
        );

        List<Studio> availableStudio = studioRepo.findAvailableStudio(occupiedStudios);

        if (availableStudio.isEmpty()) {
            throw new BookingException("No studio found for the time interval");
        }

        List<ServiceAssign> serviceAssigns = new ArrayList<>();

        for (String serviceId : req.getServiceIds()) {
            serviceAssigns.add(serviceAssignService.create(ServiceAssignRequest.builder()
                    .serviceId(serviceId)
                    .build()));
        }

        Double serviceTotal = serviceAssigns.stream()
                .mapToDouble(sa -> sa.getService().getServiceFee())
                .sum();

        return StudioAssign.builder()
                .studio(availableStudio.getFirst())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .studioAmount(req.getStudioAmount())
                .serviceAmount(serviceTotal)
                .additionTime(req.getAdditionTime())
                .status(req.getStatus() != null ? req.getStatus() : AssignStatus.COMING_SOON)
                .serviceAssigns(serviceAssigns)
                .build();
    }

    @Override
    public List<StudioAssign> createList(List<StudioAssignRequest> requests) {
        List<StudioAssign> studioAssigns = new ArrayList<>();
        for (StudioAssignRequest req : requests) {
            studioAssigns.add(create(req));
        }
        return studioAssigns;
    }


    @Override
    public StudioAssignResponse update(String id, StudioAssignRequest req) {
        StudioAssign assign = assignRepo.findById(id)
                .orElseThrow(() -> new AccountException("StudioAssign not found with id: " + id));

        if (req.getStartTime() != null) assign.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) assign.setEndTime(req.getEndTime());
        if (req.getStudioAmount() != null) assign.setStudioAmount(req.getStudioAmount());
        if (req.getServiceAmount() != null) assign.setServiceAmount(req.getServiceAmount());
        if (req.getAdditionTime() != null) assign.setAdditionTime(req.getAdditionTime());
        if (req.getStatus() != null) assign.setStatus(req.getStatus());

        assignRepo.save(assign);
        return toResponse(assign);
    }

    @Override
    public String delete(String id) {
        StudioAssign assign = assignRepo.findById(id)
                .orElseThrow(() -> new AccountException("StudioAssign not found with id: " + id));
        assign.setStatus(AssignStatus.CANCELLED);
        assignRepo.save(assign);
        return "Studio assignment cancelled successfully!";
    }

    private StudioAssignResponse toResponse(StudioAssign entity) {
        return StudioAssignResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBooking() != null ? entity.getBooking().getId() : null)
                .studioId(entity.getStudio() != null ? entity.getStudio().getId() : null)
                .studioName(entity.getStudio() != null ? entity.getStudio().getStudioName() : null)
                .locationName(entity.getStudio() != null && entity.getStudio().getLocation() != null
                        ? entity.getStudio().getLocation().getLocationName()
                        : null)
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .studioAmount(entity.getStudioAmount())
                .serviceAmount(entity.getServiceAmount())
                .additionTime(entity.getAdditionTime())
                .status(entity.getStatus())
                .build();
    }
}
