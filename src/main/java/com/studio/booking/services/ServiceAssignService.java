package com.studio.booking.services;

import com.studio.booking.dtos.request.ServiceAssignRequest;
import com.studio.booking.dtos.response.ServiceAssignResponse;
import com.studio.booking.entities.ServiceAssign;

import java.util.List;

public interface ServiceAssignService {
    List<ServiceAssignResponse> getAll();
    List<ServiceAssignResponse> getByStudioAssign(String studioAssignId);
    List<ServiceAssignResponse> getByService(String serviceId);
    ServiceAssign create(ServiceAssignRequest req);
    ServiceAssignResponse update(String id, ServiceAssignRequest req);
    String delete(String id);
}
