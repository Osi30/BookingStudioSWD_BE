package com.studio.booking.services;

import com.studio.booking.dtos.request.ServiceRequest;
import com.studio.booking.dtos.response.ServiceResponse;

import java.util.List;

public interface ServiceService {
    List<ServiceResponse> getAll();
    ServiceResponse getById(String id);
    ServiceResponse create(ServiceRequest req);
    ServiceResponse update(String id, ServiceRequest req);
    String delete(String id);
}
