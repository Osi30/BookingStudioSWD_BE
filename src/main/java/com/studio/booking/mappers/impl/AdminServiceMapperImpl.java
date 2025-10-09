package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.AdminServiceRequest;
import com.studio.booking.dtos.response.AdminServiceResponse;
import com.studio.booking.entities.Service;
import com.studio.booking.enums.ServiceStatus;
import com.studio.booking.mappers.AdminServiceMapper;
import org.springframework.stereotype.Component;

@Component
public class AdminServiceMapperImpl implements AdminServiceMapper {
    @Override
    public Service toEntity(AdminServiceRequest req) {
        return Service.builder()
                .serviceName(req.getServiceName())
                .serviceFee(req.getServiceFee())
                .description(req.getDescription())
                .status(req.getStatus() != null ? req.getStatus() : ServiceStatus.AVAILABLE)
                .build();
    }

    @Override
    public AdminServiceResponse toResponse(Service service) {
        return AdminServiceResponse.builder()
                .id(service.getId())
                .serviceName(service.getServiceName())
                .serviceFee(service.getServiceFee())
                .description(service.getDescription())
                .status(service.getStatus())
                .build();
    }

    @Override
    public Service updateEntity(Service existing, AdminServiceRequest req) {
        if (req.getServiceName() != null) existing.setServiceName(req.getServiceName());
        if (req.getServiceFee() != null) existing.setServiceFee(req.getServiceFee());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getStatus() != null) existing.setStatus(req.getStatus());
        return existing;
    }
}
