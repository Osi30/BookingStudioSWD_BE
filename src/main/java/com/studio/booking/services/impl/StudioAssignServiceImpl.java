package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.StudioAssignResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import com.studio.booking.enums.BookingType;
import com.studio.booking.enums.ServiceAssignStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        // Validate
        validationTime(req.getStartTime(), req.getEndTime());

        // Find currently available studio
        Studio availableStudio = getAvailableStudioByTime(req.getLocationId(), req.getStudioTypeId(), req);

        // Price of Studio
        Double studioAmount = priceTableItemService
                .getPriceByTypeAndTime(req.getStudioTypeId(), req.getStartTime(), req.getEndTime())
                .getTotalPrice();

        // Services
        List<ServiceAssign> serviceAssigns = new ArrayList<>();
        double serviceTotal = 0D;
        if (Validation.isValidCollection(req.getServiceIds())) {
            serviceAssigns = serviceAssignService.createByList(req.getServiceIds());
            serviceTotal = serviceAssigns.stream()
                    .mapToDouble(sa -> sa.getService().getServiceFee())
                    .sum();
        }

        return StudioAssign.builder()
                .studio(availableStudio)
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

        if (!assign.getStatus().equals(AssignStatus.COMING_SOON)) {
            throw new BookingException("StudioAssign is not in coming status");
        }

        double updatedAmount = 0D;

        // Start Time and End Time
        if (req.getStartTime() != null && req.getEndTime() != null) {
            Double currentStudioAmount = assign.getStudioAmount();
            updateTimeInterval(assign, req);
            updatedAmount = assign.getStudioAmount() - currentStudioAmount;
        }

        // Services
        if (Validation.isValidCollection(req.getServiceIds())) {
            List<ServiceAssign> serviceAssigns = serviceAssignService.createByList(req.getServiceIds());
            double serviceTotal = serviceAssigns.stream()
                    .mapToDouble(sa -> sa.getService().getServiceFee())
                    .sum();

            assign.getServiceAssigns().addAll(serviceAssigns);
            updatedAmount += serviceTotal;
        }

        // Update Booking
        assign.getBooking().setTotal(assign.getBooking().getTotal() + updatedAmount);

        StudioAssignResponse response = toResponse(assignRepo.save(assign));
        response.setUpdatedAmount(updatedAmount);
        return response;
    }

    @Override
    public String delete(String id) {
        StudioAssign assign = assignRepo.findById(id)
                .orElseThrow(() -> new BookingException("StudioAssign not found with id: " + id));

        if (!assign.getStatus().equals(AssignStatus.COMING_SOON)) {
            throw new BookingException("StudioAssign is not in coming status");
        }

        validationRequestTime(2, assign.getStartTime());

        // Update Booking (Minus Amount)
        Booking booking = assign.getBooking();
        booking.setTotal(booking.getTotal() - (assign.getServiceAmount() + assign.getStudioAmount()));

        // Update Services
        assign.getServiceAssigns()
                .forEach(s -> s.setStatus(ServiceAssignStatus.CANCELLED));

        // Update Assign Studio Status
        assign.setStatus(booking.getBookingType().equals(BookingType.PAY_FULL)
                ? AssignStatus.AWAITING_REFUND : AssignStatus.CANCELLED);
        assignRepo.save(assign);
        return "Studio assignment cancelled successfully!";
    }

    private void updateTimeInterval(StudioAssign assign, StudioAssignRequest req) {
        validationTime(assign.getStartTime(), assign.getEndTime());

        String studioTypeId = assign.getStudio().getStudioType().getId();
        String locationId = assign.getStudio().getLocation().getId();

        Studio availableStudio = getAvailableStudioByTime(
                locationId,
                studioTypeId,
                req);

        Double studioAmount = priceTableItemService
                .getPriceByTypeAndTime(studioTypeId, req.getStartTime(), req.getEndTime())
                .getTotalPrice();

        assign.setStartTime(req.getStartTime());
        assign.setEndTime(req.getEndTime());
        assign.setStudioAmount(studioAmount);
        assign.setStudio(availableStudio);
    }

    private Studio getAvailableStudioByTime(String locationId, String typeId, StudioAssignRequest req) {
        // Minus Buffer minutes
        LocalDateTime bufferStartTime = updateStartTimeWithBuffer(req.getBufferMinutes(), req.getStartTime());

        // Find currently occupied studios
        Set<String> occupiedStudios = studioRepo.findOccupiedStudioIds(
                locationId, typeId,
                bufferStartTime, req.getEndTime()
        );

        // Find currently available studios (N - No)
        List<Studio> availableStudio = studioRepo.findAvailableStudio(occupiedStudios);

        if (availableStudio.isEmpty()) {
            throw new BookingException("No studio found for the time interval: " +
                    req.getStartTime() + " - " + req.getEndTime());
        }

        return availableStudio.getFirst();
    }

    private void validationTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new BookingException("Start time cannot be after end time");
        }

        validationRequestTime(2, startTime);
    }

    private void validationRequestTime(int dayMax, LocalDateTime startTime) {
        long dayBetween = ChronoUnit.DAYS.between(LocalDate.now(), startTime);
        if (dayBetween <= dayMax) {
            throw new BookingException("Cannot update time within 2 two days before start time");
        }
    }

    /// Minus buffer time (apply for start time)
    private LocalDateTime updateStartTimeWithBuffer(Long buffer, LocalDateTime startTime) {
        // For checking if it back to the yesterday
        LocalDateTime startTimeAfterBuffer = startTime.minusMinutes(buffer);
        if (startTimeAfterBuffer.toLocalDate() != startTime.toLocalDate()) {
            startTimeAfterBuffer = startTime.toLocalDate().atStartOfDay();
        }

        return startTimeAfterBuffer;
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
