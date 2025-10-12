package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.request.ServiceRequest;
import com.studio.booking.dtos.response.ServiceResponse;
import com.studio.booking.entities.Service;
import com.studio.booking.enums.ServiceStatus;
import com.studio.booking.mappers.ServiceMapper;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapperImpl implements ServiceMapper {
    @Override
    public Service toEntity(ServiceRequest req) {
        return Service.builder()
                .serviceName(req.getServiceName())
                .serviceFee(req.getServiceFee())
                .description(req.getDescription())
                .status(req.getStatus() != null ? req.getStatus() : ServiceStatus.AVAILABLE)
                .build();
    }

    @Override
    public ServiceResponse toResponse(Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .serviceName(service.getServiceName())
                .serviceFee(service.getServiceFee())
                .description(service.getDescription())
                .status(service.getStatus())
                .build();
    }

    @Override
    public Service updateEntity(Service existing, ServiceRequest req) {
        if (req.getServiceName() != null) existing.setServiceName(req.getServiceName());
        if (req.getServiceFee() != null) existing.setServiceFee(req.getServiceFee());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getStatus() != null) existing.setStatus(req.getStatus());
        return existing;
    }
}
