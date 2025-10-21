package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.request.UpdateAdditionalTimeRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.dtos.response.StudioAssignAdditionTimeResponse;
import com.studio.booking.dtos.response.StudioAssignResponse;
import com.studio.booking.dtos.response.StudioResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import com.studio.booking.enums.BookingStatus;
import com.studio.booking.enums.StudioStatus;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.repositories.StudioAssignRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.services.PriceTableItemService;
import com.studio.booking.services.ServiceAssignService;
import com.studio.booking.services.StudioAssignService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import com.studio.booking.exceptions.exceptions.AccountException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class StudioAssignServiceImpl implements StudioAssignService {
    private final ServiceAssignService serviceAssignService;
    private final PriceTableItemService priceTableItemService;
    private final StudioAssignRepo assignRepo;
    private final StudioRepo studioRepo;
    private final BookingRepo bookingRepo;
    private final StudioAssignRepo studioAssignRepo;

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
        // Minus Buffer minutes
        Long bufferMinutes = req.getBufferMinutes();
        // For checking if it back to the yesterday
        LocalDateTime startTimeAfterBuffer = req.getStartTime().minusMinutes(bufferMinutes);
        if (startTimeAfterBuffer.toLocalDate() != req.getStartTime().toLocalDate()) {
            req.setStartTime(req.getStartTime().toLocalDate().atStartOfDay());
        }

        // Find currently occupied studios
        Set<String> occupiedStudios = studioRepo.findOccupiedStudioIds(
                req.getLocationId(), req.getStudioTypeId(),
                req.getStartTime(), req.getEndTime()
        );

        // Find currently available studios (N - No)
        List<Studio> availableStudio = studioRepo.findAvailableStudio(occupiedStudios);

        if (availableStudio.isEmpty()) {
            throw new BookingException("No studio found for the time interval");
        }

        Double studioAmount = priceTableItemService
                .getPriceByTypeAndTime(req.getStudioTypeId(), req.getStartTime(), req.getEndTime())
                .getTotalPrice();

        List<ServiceAssign> serviceAssigns = new ArrayList<>();
        double serviceTotal = 0D;
        if (Validation.isValidCollection(req.getServiceIds())) {
            serviceAssigns = serviceAssignService.createByList(req.getServiceIds());
            serviceTotal = serviceAssigns.stream()
                    .mapToDouble(sa -> sa.getService().getServiceFee())
                    .sum();
        }

        return StudioAssign.builder()
                .studio(availableStudio.getFirst())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .studioAmount(studioAmount)
                .serviceAmount(serviceTotal)
                .additionTime(req.getAdditionTime())
                .status(req.getStatus() != null ? req.getStatus() : AssignStatus.COMING_SOON)
                .serviceAssigns(serviceAssigns)
                .build();
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

    @Override
    public StudioAssignResponse updateStatus(String id, UpdateStatusRequest request) {
        var studioAssign = studioAssignRepo.findById(id)
                .orElseThrow(() -> new BookingException("Studio Assign not found with id: " + id));

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new BookingException("Studio status cannot be null/blank");
        }

        AssignStatus newStatus;
        try {
            newStatus = AssignStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BookingException("Invalid StudioStatus: " + request.getStatus());
        }

        studioAssign.setStatus(newStatus);
        studioAssignRepo.save(studioAssign);

        if (newStatus.equals(AssignStatus.ENDED))
        {
            boolean stillOpen = studioAssignRepo.existsByBooking_IdAndStatusNot(studioAssign.getBooking().getId(), AssignStatus.ENDED);
            System.out.println(stillOpen);
            if(!stillOpen)
            {
                var booking = bookingRepo.findBookingById(studioAssign.getBooking().getId());
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepo.save(booking);
            }
        }else
        {
            var booking = bookingRepo.findBookingById(studioAssign.getBooking().getId());
            booking.setStatus(BookingStatus.IN_PROGRESS);
            bookingRepo.save(booking);
        }

        return toResponse(studioAssign);
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

    @Override
    @Transactional
    public StudioAssignAdditionTimeResponse addAdditionTime(String assignId, UpdateAdditionalTimeRequest req) {
        if (req.getAdditionMinutes() == null || req.getAdditionMinutes() <= 0) {
            throw new BookingException("additionMinutes must be > 0");
        }

        // 1) Lock assign để cập nhật
        StudioAssign assign = assignRepo.findByIdForUpdate(assignId)
                .orElseThrow(() -> new BookingException("StudioAssign not found with id: " + assignId));

        if (assign.getStartTime() == null || assign.getEndTime() == null) {
            throw new BookingException("Assign missing start/end time");
        }

        // 2) Tính thêm phí dựa trên studioType
        String studioTypeId = null;
        if (assign.getStudio() != null && assign.getStudio().getStudioType() != null) {
            studioTypeId = assign.getStudio().getStudioType().getId();
        } else if (assign.getBooking() != null && assign.getBooking().getStudioType() != null) {
            studioTypeId = assign.getBooking().getStudioType().getId();
        }
        if (studioTypeId == null) {
            throw new BookingException("Cannot resolve studio type for pricing");
        }

        var oldEnd = assign.getEndTime();
        long minutes = req.getAdditionMinutes();
        double extraFee = priceTableItemService.getAdditionalPrice(studioTypeId, oldEnd, minutes);

        // 3) Cập nhật assign
        assign.setEndTime(oldEnd.plusMinutes(minutes));
        assign.setAdditionTime((assign.getAdditionTime() == null ? 0D : assign.getAdditionTime()) + minutes);
        assign.setStudioAmount((assign.getStudioAmount() == null ? 0D : assign.getStudioAmount()) + extraFee);
        assignRepo.save(assign);

        // 4) Recalc booking.total (nếu assign thuộc booking)
        String bookingId = null;
        Double newBookingTotal = null;
        if (assign.getBooking() != null) {
            bookingId = assign.getBooking().getId();
            double total = studioAssignRepo
                    .sumAmountsByBookingIdAndStatusNot(bookingId, AssignStatus.CANCELLED);
            var booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new BookingException("Booking not found with id: " ));//+ bookingId));
            booking.setTotal(total);
            bookingRepo.save(booking);
            newBookingTotal = total;
        }

        return StudioAssignAdditionTimeResponse.builder()
                .assignId(assign.getId())
                .oldEndTime(oldEnd)
                .newEndTime(assign.getEndTime())
                .addedMinutes(minutes)
                .addedFee(extraFee)
                .newStudioAmount(assign.getStudioAmount())
                .bookingId(bookingId)
                .newBookingTotal(newBookingTotal)
                .build();
    }
}
