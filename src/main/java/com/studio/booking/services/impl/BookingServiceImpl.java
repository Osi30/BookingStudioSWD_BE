package com.studio.booking.services.impl;

import com.studio.booking.dtos.dto.NotificationDTO;
import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.*;

import com.studio.booking.enums.AccountRole;

import com.studio.booking.enums.AssignStatus;

import com.studio.booking.enums.BookingStatus;
import com.studio.booking.enums.BookingType;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.mappers.BookingMapper;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.repositories.FcmTokenRepo;
import com.studio.booking.services.*;
import com.studio.booking.utils.Validation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final StudioTypeService studioTypeService;
    private final StudioAssignService studioAssignService;
    private final LocationService locationService;
    private final AccountService accountService;
    private final NotificationService notificationService;
    private final BookingRepo bookingRepo;
    private final FcmTokenRepo fcmTokenRepo;
    private final BookingMapper mapper;

    @Override
    public Booking createBooking(String accountId, BookingRequest bookingRequest) {
        // Validation
        if (Validation.isNullOrEmpty(bookingRequest.getStudioTypeId())) {
            throw new BookingException("Studio Type is required");
        }

        if (bookingRequest.getStudioAssignRequests().isEmpty()) {
            throw new BookingException("Studio Quantity is required");
        }

        if (Validation.isNullOrEmpty(bookingRequest.getPhoneNumber())) {
            throw new BookingException("Phone Number is required");
        }

        // Studio Type
        StudioType studioType = studioTypeService.getById(bookingRequest.getStudioTypeId());
        Long bufferMinutes = (long) (double) studioType.getBufferTime();

        // Location
        Location location = locationService.getById(bookingRequest.getLocationId());

        Booking booking = mapper.toBooking(bookingRequest);
        booking.setStatus(BookingStatus.AWAITING_PAYMENT);
        booking.setStudioType(studioType);

        // Studio Assigns
        List<StudioAssign> studioAssigns = new ArrayList<>();
        for (StudioAssignRequest req : bookingRequest.getStudioAssignRequests()) {
            req.setStudioTypeId(studioType.getId());
            req.setLocationId(location.getId());
            req.setBufferMinutes(bufferMinutes);
            StudioAssign studioAssign = studioAssignService.create(req);
            studioAssign.setBooking(booking);
            studioAssigns.add(studioAssign);
        }

        booking.setStudioAssigns(studioAssigns);
        booking.setTotal(studioAssigns
                .stream().mapToDouble(sa -> sa.getStudioAmount() + sa.getServiceAmount())
                .sum()
        );

        // Account
        booking.setAccount(accountService.getAccountById(accountId));

        return bookingRepo.save(booking);
    }

    @Override
    public List<BookingResponse> getAll() {
        return bookingRepo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByAccount(String accountId) {
        return bookingRepo.findAllByAccount_Id(accountId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BookingResponse getById(String id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));
        return mapper.toResponse(booking);
    }

    @Override
    public BookingResponse updateStatus(String id, BookingStatusRequest req) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));

        if (req.getStatus() == null || booking.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new IllegalArgumentException("Booking status cannot be null");
        }

        booking.setStatus(req.getStatus());
        if (req.getNote() != null && !req.getNote().isEmpty()) {
            booking.setNote(req.getNote());
        }

        bookingRepo.save(booking);
        return mapper.toResponse(booking);
    }

    @Override
    public BookingResponse updateBooking(String id, BookingRequest req) {
        Booking booking = getBookingById(id);
        booking = mapper.updateBooking(booking, req);

        Double updatedAmount = 0D;
        // Assign new studio
        if (Validation.isValidCollection(req.getStudioAssignRequests())) {
            List<StudioAssign> studioAssigns = new ArrayList<>();
            for (StudioAssignRequest request : req.getStudioAssignRequests()) {
                request.setStudioTypeId(req.getStudioTypeId());
                request.setLocationId(req.getLocationId());
                request.setBufferMinutes(booking.getStudioType().getBufferTime().longValue());
                StudioAssign studioAssign = studioAssignService.create(request);
                studioAssign.setBooking(booking);
                studioAssigns.add(studioAssign);
            }

            Double total = studioAssigns
                    .stream().mapToDouble(sa -> sa.getStudioAmount() + sa.getServiceAmount())
                    .sum();
            updatedAmount = total;
            booking.getStudioAssigns().addAll(studioAssigns);
            booking.setTotal(booking.getTotal() + total);
        }

        BookingResponse response = mapper.toResponse(bookingRepo.save(booking));
        response.setUpdatedAmount(updatedAmount);
        return response;
    }

    @Override
    public Booking cancelBooking(String id, String note) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));

        // Validation
        if (!booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new BookingException("Booking is not in confirmed for cancellation");
        }

        List<StudioAssign> activeStudioAssigns = booking.getStudioAssigns()
                .stream().filter(sa -> sa.getStatus().equals(AssignStatus.IS_HAPPENING) || sa.isOutOfUpdated())
                .toList();
        if (Validation.isValidCollection(activeStudioAssigns)) {
            throw new BookingException("Some studio assign in booking is already in progress");
        }

        // If pay full then customer will be refund, if deposit then booking is being cancelled
        if (booking.getBookingType().equals(BookingType.PAY_FULL)) {
            booking.setStatus(BookingStatus.AWAITING_REFUND);
            booking.getStudioAssigns().stream()
                    .filter(s -> s.getStatus().equals(AssignStatus.COMING_SOON))
                    .forEach(s -> s.setStatus(AssignStatus.AWAITING_REFUND));
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            booking.getStudioAssigns().stream()
                    .filter(s -> s.getStatus().equals(AssignStatus.COMING_SOON))
                    .forEach(s -> s.setStatus(AssignStatus.CANCELLED));
        }
        return bookingRepo.save(booking);
    }


    @Override
    public List<BookingResponse> getForEmployee(String employeeAccountId) {
        Account employee = accountService.getAccountById(employeeAccountId);
        if (employee.getRole() != AccountRole.STAFF) {
            throw new BookingException("Only staff can view bookings by location");
        }
        if (employee.getLocation() == null) {
            throw new BookingException("Staff does not have a location assigned");
        }

        String locationId = employee.getLocation().getId();
        return bookingRepo.findAllByLocationId(locationId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void sendBookingNotificationToStaff(Booking booking) throws MessagingException {
        String locationId = booking.getStudioAssigns()
                .getFirst().getStudio().getLocation().getId();

        Map<String, String> data = Map.of(
                "screen", "booking_detail",
                "booking_id", booking.getId()
        );

        List<String> tokens = fcmTokenRepo
                .findAllByRoleStaffAndLocation(AccountRole.STAFF, locationId)
                .stream().map(FcmToken::getToken).toList();

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .tokens(tokens)
                .title("Booking Mới")
                .body("Có một booking mới. Nhấp để xem chi tiết.")
                .data(data)
                .build();

        notificationService.sendNotificationToUser(notificationDTO);
    }

    private Booking getBookingById(String id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));

    }
}
