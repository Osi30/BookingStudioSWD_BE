package com.studio.booking.mappers;

import com.studio.booking.dtos.request.AdminServiceRequest;
import com.studio.booking.dtos.response.AdminServiceResponse;
import com.studio.booking.entities.Service;

public interface AdminServiceMapper {
    Service toEntity(AdminServiceRequest req);
    AdminServiceResponse toResponse(Service service);
    Service updateEntity(Service existing, AdminServiceRequest req);
}
