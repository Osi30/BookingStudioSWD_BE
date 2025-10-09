package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.response.AdminBookingResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.mappers.AdminBookingMapper;
import org.springframework.stereotype.Component;

@Component
public class AdminBookingMapperImpl implements AdminBookingMapper {
    @Override
    public AdminBookingResponse toResponse(Booking booking) {
        return AdminBookingResponse.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .updateDate(booking.getUpdateDate())
                .note(booking.getNote())
                .total(booking.getTotal())
                .status(booking.getStatus())
                .bookingType(booking.getBookingType())
                .accountEmail(booking.getAccount() != null ? booking.getAccount().getEmail() : null)
                .accountName(booking.getAccount() != null ? booking.getAccount().getFullName() : null)
                .studioTypeName(booking.getStudioType() != null ? booking.getStudioType().getName() : null)
                .build();
    }
}
