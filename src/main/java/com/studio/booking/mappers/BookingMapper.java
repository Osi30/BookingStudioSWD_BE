package com.studio.booking.mappers;

import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.Booking;

public interface BookingMapper {
    Booking toBooking(BookingRequest bookingRequest);
    BookingResponse toResponse(Booking booking);
}
