package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.StudioAssignResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.Studio;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
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
    @Transactional
    public StudioAssignResponse attachStudioToExistingAssign(String assignId, String studioId) {
        StudioAssign assign = assignRepo.findByIdForUpdate(assignId)
                .orElseThrow(() -> new BookingException("StudioAssign not found with id: " + assignId));

        String oldStudioId = assign.getStudio().getId();

        Studio studio = studioRepo.findByIdForUpdate(studioId)
                .orElseThrow(() -> new BookingException("Studio not found with id: " + studioId));

        if (studio.getStatus() != StudioStatus.AVAILABLE) {
            throw new BookingException("Studio is not AVAILABLE for assignment");
        }
        assign.setStudio(studio);
        assign.setStatus(AssignStatus.IS_HAPPENING);
        studio.setStatus(StudioStatus.DELETED); //Ko enum được chỗ này oh shiet nên để tạm qua deleted

        //Cập nhật lại old studio
        if(!oldStudioId.isEmpty() && !oldStudioId.equals(studioId))
        {
            Studio oldStudio = studioRepo.findByIdForUpdate(oldStudioId)
                    .orElseThrow(() -> new BookingException("Old Studio not found with id: " + oldStudioId));
            oldStudio.setStatus(StudioStatus.AVAILABLE);
        }

        studioRepo.save(studio);
        assignRepo.save(assign);

        return toResponse(assign);
    }
//    @Override
//    @Transactional
//    public StudioAssign assignStudio(String studioId, StudioAssignRequest req) {
//        Studio studio = studioRepo.findByIdForUpdate(studioId)
//                .orElseThrow(() -> new BookingException("Studio not found with id: " + studioId));
//
//        //cho phép khi AVAILABLE
//        if (studio.getStatus() != StudioStatus.AVAILABLE) {
//            throw new BookingException("Studio is not AVAILABLE for assigning");
//        }
//
//        Long bufferMinutes = req.getBufferMinutes();
//        if (bufferMinutes != null && bufferMinutes > 0) {
//            LocalDateTime startTimeAfterBuffer = req.getStartTime().minusMinutes(bufferMinutes);
//            if (!startTimeAfterBuffer.toLocalDate().equals(req.getStartTime().toLocalDate())) {
//                req.setStartTime(req.getStartTime().toLocalDate().atStartOfDay());
//            }
//        }
//
//        Double studioAmount = priceTableItemService
//                .getPriceByTypeAndTime(req.getStudioTypeId(), req.getStartTime(), req.getEndTime())
//                .getTotalPrice();
//
//        //Gán service nếu có
//        List<ServiceAssign> serviceAssigns = new ArrayList<>();
//        double serviceTotal = 0D;
//        if (Validation.isValidCollection(req.getServiceIds())) {
//            serviceAssigns = serviceAssignService.createByList(req.getServiceIds());
//            serviceTotal = serviceAssigns.stream()
//                    .mapToDouble(sa -> sa.getService().getServiceFee())
//                    .sum();
//        }
//
//        StudioAssign assign = StudioAssign.builder()
//                .studio(studio)
//                .startTime(req.getStartTime())
//                .endTime(req.getEndTime())
//                .studioAmount(studioAmount)
//                .serviceAmount(serviceTotal)
//                .additionTime(req.getAdditionTime())
//                .status(req.getStatus() != null ? req.getStatus() : AssignStatus.COMING_SOON)
//                .serviceAssigns(serviceAssigns)
//                .build();
//
//        if (!Validation.isNullOrEmpty(req.getBookingId())) {
//            Booking booking = bookingRepo.findById(req.getBookingId())
//                    .orElseThrow(() -> new BookingException("Booking not found with id: " + req.getBookingId()));
//            assign.setBooking(booking);
//        }
//
//        // (7) Đổi trạng thái studio → OCCUPIED rồi save
//        studio.setStatus(StudioStatus.OCCUPIED);
//        studioRepo.save(studio);
//
//        // (8) Lưu assign
//        return assignRepo.save(assign);
//    }


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
