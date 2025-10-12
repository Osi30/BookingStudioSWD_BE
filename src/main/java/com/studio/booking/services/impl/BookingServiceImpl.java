package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.enums.BookingStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.BookingMapper;
import com.studio.booking.repositories.BookingRepo;
import com.studio.booking.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final BookingMapper mapper;

    @Override
    public List<BookingResponse> getAll() {
        return bookingRepo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BookingResponse getById(String id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new AccountException("Booking not found with id: " + id));
        return mapper.toResponse(booking);
    }

    @Override
    public BookingResponse updateStatus(String id, BookingStatusRequest req) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new AccountException("Booking not found with id: " + id));

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
                .orElseThrow(() -> new AccountException("Booking not found with id: " + id));

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setNote(note != null ? note : "Cancelled by admin");
        bookingRepo.save(booking);
        return "Booking cancelled successfully!";
    }
}
