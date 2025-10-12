
package com.studio.booking.services.impl;
import com.studio.booking.dtos.request.ServiceRequest;
import com.studio.booking.dtos.response.ServiceResponse;
import com.studio.booking.enums.ServiceStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.ServiceMapper;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.services.ServiceService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@org.springframework.stereotype.Service  // ⚠ dùng fully-qualified để tránh xung đột
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepo serviceRepo;
    private final ServiceMapper mapper;

    @Override
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
    public ServiceResponse create(ServiceRequest req) {
        com.studio.booking.entities.Service serviceEntity = mapper.toEntity(req);
        serviceRepo.save(serviceEntity);
        return mapper.toResponse(serviceEntity);
    }

    @Override
    public ServiceResponse update(String id, ServiceRequest req) {
        com.studio.booking.entities.Service existing = serviceRepo.findById(id)
                .orElseThrow(() -> new AccountException("Service not found with id: " + id));
        existing = mapper.updateEntity(existing, req);
        serviceRepo.save(existing);
        return mapper.toResponse(existing);
    }

    @Override
    public String delete(String id) {
        com.studio.booking.entities.Service serviceEntity = serviceRepo.findById(id)
                .orElseThrow(() -> new AccountException("Service not found with id: " + id));
        serviceEntity.setStatus(ServiceStatus.DELETED);
        serviceRepo.save(serviceEntity);
        return "Service deleted successfully!";
    }
}
