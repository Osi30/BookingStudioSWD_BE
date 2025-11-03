
package com.studio.booking.services.impl;
import com.studio.booking.dtos.request.ServiceRequest;
import com.studio.booking.dtos.request.UpdateStatusRequest;
import com.studio.booking.dtos.response.ServiceResponse;
import com.studio.booking.entities.Service;
import com.studio.booking.enums.ServiceStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.mappers.ServiceMapper;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.services.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepo serviceRepo;
    private final ServiceMapper mapper;

    @Override
    @Cacheable(value = "services", key = "'AllServices'")
    public List<ServiceResponse> getAll() {
        return serviceRepo.findAllByStatusNot(ServiceStatus.DELETED).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ServiceResponse getById(String id) {
        com.studio.booking.entities.Service serviceEntity = serviceRepo.findById(id)
                .orElseThrow(() -> new AccountException("Service not found with id: " + id));
        return mapper.toResponse(serviceEntity);
    }

    @Override
    @CacheEvict(value = {"services"}, allEntries = true)
    public ServiceResponse create(ServiceRequest req) {
        com.studio.booking.entities.Service serviceEntity = mapper.toEntity(req);
        serviceRepo.save(serviceEntity);
        return mapper.toResponse(serviceEntity);
    }

    @Override
    @CacheEvict(value = {"services"}, allEntries = true)
    public ServiceResponse update(String id, ServiceRequest req) {
        com.studio.booking.entities.Service existing = serviceRepo.findById(id)
                .orElseThrow(() -> new AccountException("Service not found with id: " + id));
        existing = mapper.updateEntity(existing, req);
        serviceRepo.save(existing);
        return mapper.toResponse(existing);
    }

    @Override
    @CacheEvict(value = {"services"}, allEntries = true)
    public String delete(String id) {
        com.studio.booking.entities.Service serviceEntity = serviceRepo.findById(id)
                .orElseThrow(() -> new AccountException("Service not found with id: " + id));
        serviceEntity.setStatus(ServiceStatus.DELETED);
        serviceRepo.save(serviceEntity);
        return "Service deleted successfully!";
    }

    @Override
    @CacheEvict(value = {"services"}, allEntries = true)
    public ServiceResponse updateStatus(String id, UpdateStatusRequest req) {
        Service service = serviceRepo.findById(id)
                .orElseThrow(() -> new BookingException("Service not found with id: " + id));

        if (req.getStatus() == null || req.getStatus().isBlank()) {
            throw new BookingException("Service status cannot be null/blank");
        }

        ServiceStatus newStatus;
        try {
            newStatus = ServiceStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BookingException("Invalid ServiceStatus: " + req.getStatus());
        }

        service.setStatus(newStatus);
        serviceRepo.save(service);

        return mapper.toResponse(service);
    }
}
