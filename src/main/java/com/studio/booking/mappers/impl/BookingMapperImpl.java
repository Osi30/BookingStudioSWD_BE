package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.BookingRequest;
import com.studio.booking.dtos.response.BookingResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.mappers.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingMapperImpl implements BookingMapper {
    private final ModelMapper modelMapper;

    @Override
    public Booking toBooking(BookingRequest bookingRequest) {
        return modelMapper.map(bookingRequest, Booking.class);
    }

    @Override
    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
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
                .phoneNumber(booking.getPhoneNumber())
                .build();
    }

    @Override
    public Booking updateBooking(Booking booking, BookingRequest req) {
        Optional.ofNullable(req.getNote()).ifPresent(booking::setNote);
        Optional.ofNullable(req.getPhoneNumber()).ifPresent(booking::setPhoneNumber);
        return booking;
    }
}
