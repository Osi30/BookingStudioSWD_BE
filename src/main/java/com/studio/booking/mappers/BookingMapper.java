package com.studio.booking.mappers;

import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.Booking;

public interface BookingMapper {
    BookingResponse toResponse(Booking booking);
}
