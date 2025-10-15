package com.studio.booking.mappers;

import com.studio.booking.dtos.request.ServiceRequest;
import com.studio.booking.dtos.response.ServiceResponse;
import com.studio.booking.entities.Service;

public interface ServiceMapper {
    Service toEntity(ServiceRequest req);
    ServiceResponse toResponse(Service service);
    Service updateEntity(Service existing, ServiceRequest req);
}
