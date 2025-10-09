package com.studio.booking.mappers;

import com.studio.booking.dtos.response.AdminBookingResponse;
import com.studio.booking.entities.Booking;

public interface  AdminBookingMapper {
    AdminBookingResponse toResponse(Booking booking);
}
