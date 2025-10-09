package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminServiceAssignRequest;
import com.studio.booking.dtos.response.AdminServiceAssignResponse;

import java.util.List;

public interface AdminServiceAssignService {
    List<AdminServiceAssignResponse> getAll();
    List<AdminServiceAssignResponse> getByStudioAssign(String studioAssignId);
    List<AdminServiceAssignResponse> getByService(String serviceId);
    AdminServiceAssignResponse create(AdminServiceAssignRequest req);
    AdminServiceAssignResponse update(String id, AdminServiceAssignRequest req);
    String delete(String id);
}
