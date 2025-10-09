package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminStudioAssignRequest;
import com.studio.booking.dtos.response.AdminStudioAssignResponse;

import java.util.List;

public interface AdminStudioAssignService {
    List<AdminStudioAssignResponse> getAll();
    List<AdminStudioAssignResponse> getByBooking(String bookingId);
    List<AdminStudioAssignResponse> getByStudio(String studioId);
    AdminStudioAssignResponse create(AdminStudioAssignRequest request);
    AdminStudioAssignResponse update(String id, AdminStudioAssignRequest request);
    String delete(String id);
}
