package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminBookingStatusRequest;
import com.studio.booking.dtos.response.AdminBookingResponse;

import java.util.List;

public interface AdminBookingService {
    List<AdminBookingResponse> getAll();
    AdminBookingResponse getById(String id);
    AdminBookingResponse updateStatus(String id, AdminBookingStatusRequest req);
    String cancelBooking(String id, String note);
}
