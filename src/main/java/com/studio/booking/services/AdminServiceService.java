package com.studio.booking.services;

import com.studio.booking.dtos.request.AdminServiceRequest;
import com.studio.booking.dtos.response.AdminServiceResponse;

import java.util.List;

public interface AdminServiceService {
    List<AdminServiceResponse> getAll();
    AdminServiceResponse getById(String id);
    AdminServiceResponse create(AdminServiceRequest req);
    AdminServiceResponse update(String id, AdminServiceRequest req);
    String delete(String id);
}
