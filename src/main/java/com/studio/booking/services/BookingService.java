package com.studio.booking.services;

import com.studio.booking.dtos.request.BookingStatusRequest;
import com.studio.booking.dtos.response.BookingResponse;

import java.util.List;

public interface BookingService {
    List<BookingResponse> getAll();
    BookingResponse getById(String id);
    BookingResponse updateStatus(String id, BookingStatusRequest req);
    String cancelBooking(String id, String note);
}
