
package com.studio.booking.services.impl;
import com.studio.booking.dtos.request.AdminServiceRequest;
import com.studio.booking.dtos.response.AdminServiceResponse;
import com.studio.booking.entities.Service;
import com.studio.booking.enums.ServiceStatus;
import com.studio.booking.exceptions.exceptions.AccountException;
import com.studio.booking.mappers.AdminServiceMapper;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.services.AdminServiceService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@org.springframework.stereotype.Service  // ⚠ dùng fully-qualified để tránh xung đột
@RequiredArgsConstructor
public class AdminServiceServiceImpl implements AdminServiceService {

    private final ServiceRepo serviceRepo;
    private final AdminServiceMapper mapper;

    @Override
    public List<AdminServiceResponse> getAll() {
        return serviceRepo.findAllByStatusNot(ServiceStatus.DELETED).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public AdminServiceResponse getById(String id) {
        com.studio.booking.entities.Service serviceEntity = serviceRepo.findById(id)
                .orElseThrow(() -> new AccountException("Service not found with id: " + id));
        return mapper.toResponse(serviceEntity);
    }

    @Override
    public AdminServiceResponse create(AdminServiceRequest req) {
        com.studio.booking.entities.Service serviceEntity = mapper.toEntity(req);
        serviceRepo.save(serviceEntity);
        return mapper.toResponse(serviceEntity);
    }

    @Override
    public AdminServiceResponse update(String id, AdminServiceRequest req) {
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
