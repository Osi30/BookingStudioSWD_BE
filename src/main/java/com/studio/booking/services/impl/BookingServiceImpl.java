package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.dtos.request.StudioAssignRequest;
import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.*;
import com.studio.booking.enums.BookingStatus;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.mappers.BookingMapper;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.services.*;
import com.studio.booking.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final StudioTypeService studioTypeService;
    private final StudioAssignService studioAssignService;
    private final LocationService locationService;
    private final AccountService accountService;
    private final BookingRepo bookingRepo;
    private final BookingMapper mapper;

    @Override
    public Booking createBooking(String accountId, BookingRequest bookingRequest) {
        if (Validation.isNullOrEmpty(bookingRequest.getStudioTypeId())) {
            throw new BookingException("Studio Type is required");
        }

        if (bookingRequest.getStudioAssignRequests().isEmpty()) {
            throw new BookingException("Studio Quantity is required");
        }

        // Studio Type
        StudioType studioType = studioTypeService.getById(bookingRequest.getStudioTypeId());
        Long bufferMinutes = (long) (double) studioType.getBufferTime();

        // Location
        Location location = locationService.getById(bookingRequest.getLocationId());

        // Studio Assigns
        List<StudioAssign> studioAssigns = new ArrayList<>();
        for (StudioAssignRequest req : bookingRequest.getStudioAssignRequests()) {
            req.setStudioTypeId(studioType.getId());
            req.setLocationId(location.getId());
            req.setBufferMinutes(bufferMinutes);
            studioAssigns.add(studioAssignService.create(req));
        }

        Booking booking = mapper.toBooking(bookingRequest);
        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setStudioType(studioType);
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
    public BookingResponse getById(String id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));
        return mapper.toResponse(booking);
    }

    @Override
    public BookingResponse updateStatus(String id, BookingStatusRequest req) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));

        if (req.getStatus() == null) {
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
    public String cancelBooking(String id, String note) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingException("Booking not found with id: " + id));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setNote(note != null ? note : "Cancelled by admin");
        bookingRepo.save(booking);
        return "Booking cancelled successfully!";
    }
}
