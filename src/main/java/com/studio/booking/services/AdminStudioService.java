package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminStudioRequest;
import com.studio.booking.dtos.response.AdminStudioResponse;

import java.util.List;

public interface AdminStudioService {
    List<AdminStudioResponse> getAll();
    AdminStudioResponse getById(String id);
    AdminStudioResponse create(AdminStudioRequest req);
    AdminStudioResponse update(String id, AdminStudioRequest req);
    String delete(String id);
    String restore(String id);
}
