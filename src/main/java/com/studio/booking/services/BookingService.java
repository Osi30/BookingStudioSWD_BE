package com.studio.booking.services;

import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(String accountId, BookingRequest bookingRequest);
    List<BookingResponse> getAll();
    BookingResponse getById(String id);
    BookingResponse updateStatus(String id, BookingStatusRequest req);
    String cancelBooking(String id, String note);

    List<BookingResponse> getForEmployee(String employeeAccountId);
}
