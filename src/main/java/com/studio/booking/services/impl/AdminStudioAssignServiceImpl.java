package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AdminStudioAssignRequest;
import com.studio.booking.dtos.response.AdminStudioAssignResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.repositories.StudioAssignRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.services.AdminStudioAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.studio.booking.exceptions.exceptions.AccountException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStudioAssignServiceImpl implements AdminStudioAssignService {
    private final StudioAssignRepo assignRepo;
    private final BookingRepo bookingRepo;
    private final StudioRepo studioRepo;

    @Override
    public List<AdminStudioAssignResponse> getAll() {
        return assignRepo.findAllByStatusNot(AssignStatus.CANCELLED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AdminStudioAssignResponse> getByBooking(String bookingId) {
        return assignRepo.findAllByBooking_Id(bookingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AdminStudioAssignResponse> getByStudio(String studioId) {
        return assignRepo.findAllByStudio_Id(studioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AdminStudioAssignResponse create(AdminStudioAssignRequest req) {
        Booking booking = bookingRepo.findById(req.getBookingId())
                .orElseThrow(() -> new AccountException("Booking not found with id: " + req.getBookingId()));
        Studio studio = studioRepo.findById(req.getStudioId())
                .orElseThrow(() -> new AccountException("Studio not found with id: " + req.getStudioId()));

        StudioAssign assign = StudioAssign.builder()
                .booking(booking)
                .studio(studio)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .studioAmount(req.getStudioAmount())
                .serviceAmount(req.getServiceAmount())
                .additionTime(req.getAdditionTime())
                .status(req.getStatus() != null ? req.getStatus() : AssignStatus.COMING_SOON)
                .build();

        assignRepo.save(assign);
        return toResponse(assign);
    }

    @Override
    public AdminStudioAssignResponse update(String id, AdminStudioAssignRequest req) {
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

    private AdminStudioAssignResponse toResponse(StudioAssign entity) {
        return AdminStudioAssignResponse.builder()
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
