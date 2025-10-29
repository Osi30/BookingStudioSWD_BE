package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.AdditionalTimePriceRequest;
import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.request.UpdateAdditionalTimeRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.dtos.response.StudioAssignAdditionTimeResponse;
import com.studio.booking.dtos.response.StudioAssignResponse;

import com.studio.booking.entities.*;
import com.studio.booking.enums.*;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.repositories.PaymentRepo;

import com.studio.booking.entities.Booking;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import com.studio.booking.enums.BookingType;

import com.studio.booking.repositories.StudioAssignRepo;
import com.studio.booking.repositories.StudioRepo;
import com.studio.booking.services.PriceTableItemService;
import com.studio.booking.services.ServiceAssignService;
import com.studio.booking.services.StudioAssignService;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import com.studio.booking.exceptions.exceptions.AccountException;
import org.springframework.transaction.annotation.Transactional;

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
    private final StudioAssignRepo studioAssignRepo;
    private final PaymentRepo paymentRepo;

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
        validationTime(req.getStartTime(), req.getHour());
        LocalDateTime endTime = req.getStartTime().plusHours(req.getHour());

        // Find currently available studio
        Studio availableStudio = getAvailableStudioByTime(req.getLocationId(), req.getStudioTypeId(), req);

        // Price of Studio
        Double studioAmount = priceTableItemService
                .getPriceByTypeAndTime(req.getStudioTypeId(), req.getStartTime(), endTime)
                .getTotalPrice();

        // Assign Studio
        StudioAssign studioAssign = StudioAssign.builder()
                .studio(availableStudio)
                .startTime(req.getStartTime())
                .endTime(endTime)
                .studioAmount(studioAmount)
                .additionTime(req.getAdditionTime())
                .status(req.getStatus() != null ? req.getStatus() : AssignStatus.COMING_SOON)
                .build();

        // Services
        List<ServiceAssign> serviceAssigns = new ArrayList<>();
        double serviceTotal = 0D;
        if (Validation.isValidCollection(req.getServiceIds())) {
            serviceAssigns = serviceAssignService.createByList(req.getServiceIds());
            for (ServiceAssign serviceAssign : serviceAssigns) {
                serviceAssign.setStudioAssign(studioAssign);
            }

            serviceTotal = serviceAssigns.stream()
                    .mapToDouble(sa -> sa.getService().getServiceFee())
                    .sum();
        }

        studioAssign.setServiceAmount(serviceTotal);
        studioAssign.setServiceAssigns(serviceAssigns);

        return studioAssign;
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
        if (req.getStartTime() != null && req.getHour() != null && req.getHour() > 0) {
            Double currentStudioAmount = assign.getStudioAmount();
            updateTimeInterval(assign, req);
            updatedAmount = assign.getStudioAmount() - currentStudioAmount;
        }

        // Services
        if (Validation.isValidCollection(req.getServiceIds())) {
            List<ServiceAssign> serviceAssigns = serviceAssignService.createByList(req.getServiceIds());
            for (ServiceAssign serviceAssign : serviceAssigns) {
                serviceAssign.setStudioAssign(assign);
            }

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

        if (newStatus.equals(AssignStatus.ENDED)) {
            boolean stillOpen = studioAssignRepo.existsByBooking_IdAndStatusNot(studioAssign.getBooking().getId(), AssignStatus.ENDED);
            System.out.println(stillOpen);
            if (!stillOpen) {
                var booking = bookingRepo.findBookingById(studioAssign.getBooking().getId());
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepo.save(booking);
            }
        } else {
            var booking = bookingRepo.findBookingById(studioAssign.getBooking().getId());
            booking.setStatus(BookingStatus.IN_PROGRESS);
            bookingRepo.save(booking);
        }

        return toResponse(studioAssign);
    }

    private void updateTimeInterval(StudioAssign assign, StudioAssignRequest req) {
        validationTime(req.getStartTime(), req.getHour());

        LocalDateTime endTime = req.getStartTime().plusHours(req.getHour());
        String studioTypeId = assign.getStudio().getStudioType().getId();
        String locationId = assign.getStudio().getLocation().getId();

        Studio availableStudio = getAvailableStudioByTime(
                locationId,
                studioTypeId,
                req);

        Double studioAmount = priceTableItemService
                .getPriceByTypeAndTime(studioTypeId, req.getStartTime(), endTime)
                .getTotalPrice();

        assign.setStartTime(req.getStartTime());
        assign.setEndTime(endTime);
        assign.setStudioAmount(studioAmount);
        assign.setStudio(availableStudio);
    }

    private Studio getAvailableStudioByTime(String locationId, String typeId, StudioAssignRequest req) {
        // Minus Buffer minutes
        LocalDateTime bufferStartTime = updateStartTimeWithBuffer(req.getBufferMinutes(), req.getStartTime());
        LocalDateTime endTime = req.getStartTime().plusHours(req.getHour());

        // Find currently occupied studios
        Set<String> occupiedStudios = studioRepo.findOccupiedStudioIds(
                locationId, typeId,
                bufferStartTime, endTime
        );

        // Find currently available studios (N - No)
        List<Studio> availableStudio = studioRepo.findAvailableStudio(occupiedStudios, locationId);

        if (availableStudio.isEmpty()) {
            throw new BookingException("No studio found for the time interval: " +
                    req.getStartTime() + " - " + endTime);
        }

        return availableStudio.getFirst();
    }

    private void validationTime(LocalDateTime startTime, Integer hour) {
        if (hour == null || hour <= 0) {
            throw new BookingException("Hour must be greater than 0");
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

    @Override
    @Transactional
    public StudioAssignAdditionTimeResponse addAdditionTime(String assignId, UpdateAdditionalTimeRequest req) {
        if (req.getAdditionMinutes() == null || req.getAdditionMinutes() < 0) {
            throw new BookingException("additionMinutes must be >= 0");
        }

        StudioAssign assign = assignRepo.findByIdForUpdate(assignId)
                .orElseThrow(() -> new BookingException("StudioAssign not found with id: " + assignId));

        if (assign.getStartTime() == null || assign.getEndTime() == null) {
            throw new BookingException("Assign missing start/end time");
        }

        String studioTypeId = null;
        if (assign.getStudio() != null && assign.getStudio().getStudioType() != null) {
            studioTypeId = assign.getStudio().getStudioType().getId();
        } else if (assign.getBooking() != null && assign.getBooking().getStudioType() != null) {
            studioTypeId = assign.getBooking().getStudioType().getId();
        }
        if (studioTypeId == null) {
            throw new BookingException("Cannot resolve studio type for pricing");
        }

        var start = assign.getStartTime();
        var oldEnd = assign.getEndTime();
        long oldAddMinutes = assign.getAdditionTime() == null ? 0L : assign.getAdditionTime().longValue();

        var baseEnd = oldEnd.minusMinutes(oldAddMinutes);   // kết thúc gốc (chưa cộng thêm)
        long newAddMinutes = req.getAdditionMinutes();      // addition mới (có thể = 0)

        double baseAmount = priceTableItemService
                .getPriceByTypeAndTime(studioTypeId, start, baseEnd)
                .getTotalPrice();

        AdditionalTimePriceRequest reqPreview = new AdditionalTimePriceRequest();
        reqPreview.setStudioTypeId(studioTypeId);
        reqPreview.setAtTime(baseEnd);
        reqPreview.setAdditionMinutes(newAddMinutes);
        double extraFee = priceTableItemService.previewAdditionalPrice(reqPreview).getExtraFee();

        double newStudioAmount = baseAmount + extraFee;

        assign.setAdditionTime((double) newAddMinutes);
        assign.setStudioAmount(newStudioAmount);
        assign.setEndTime(baseEnd.plusMinutes(newAddMinutes));
        assignRepo.save(assign);

        String bookingId = null;
        Double newBookingTotal = null;

        if (assign.getBooking() != null) {
            bookingId = assign.getBooking().getId();

            // Tính lại tổng booking từ tất cả assign (trừ CANCELLED)
            double total = assignRepo
                    .sumAmountsByBookingIdAndStatusNot(bookingId, AssignStatus.CANCELLED);

            Booking booking = bookingRepo.findById(bookingId)
                    .orElseThrow(() -> new BookingException("Booking not found with id"));
            booking.setTotal(total);
//            bookingRepo.save(booking);
            newBookingTotal = booking.getTotal();

            // Tìm payment ADDITION_PAYMENT để update, nếu không có thì tạo mới
            var opt = paymentRepo.findTopByBooking_IdAndPaymentTypeOrderByPaymentDateDesc(
                    bookingId, PaymentType.ADDITION_PAYMENT);

            if (opt.isPresent()) {
                Payment p = opt.get();
                p.setAmount(extraFee); // chỉ phần phát sinh
                paymentRepo.save(p);
            } else {
                Payment newP = new Payment();
                newP.setAmount(extraFee); // chỉ phần phát sinh
                newP.setPaymentType(PaymentType.ADDITION_PAYMENT);
                newP.setStatus(PaymentStatus.PENDING);
                newP.setBooking(booking);

                // copy paymentMethod gần nhất nếu có
                paymentRepo.findTopByBooking_IdOrderByPaymentDateDesc(bookingId)
                        .ifPresent(last -> newP.setPaymentMethod(last.getPaymentMethod()));

                newP.setPaymentDate(LocalDateTime.now());
                paymentRepo.save(newP);
            }
        }

        return StudioAssignAdditionTimeResponse.builder()
                .assignId(assign.getId())
                .addedMinutes(newAddMinutes)
                .addedFee(extraFee)
                .newStudioAmount(newStudioAmount)
                .bookingId(bookingId)
                .newBookingTotal(newBookingTotal)
                .build();
    }

}
