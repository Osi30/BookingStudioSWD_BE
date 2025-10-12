package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.ServiceAssignRequest;
import com.studio.booking.dtos.response.ServiceAssignResponse;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.repositories.ServiceAssignRepo;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.repositories.StudioAssignRepo;
import com.studio.booking.services.ServiceAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.studio.booking.exceptions.exceptions.AccountException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceAssignServiceImpl implements ServiceAssignService {
    private final ServiceAssignRepo serviceAssignRepo;
    private final ServiceRepo serviceRepo;
    private final StudioAssignRepo studioAssignRepo;

    @Override
    public List<ServiceAssignResponse> getAll() {
        return serviceAssignRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ServiceAssignResponse> getByStudioAssign(String studioAssignId) {
        return serviceAssignRepo.findAllByStudioAssign_Id(studioAssignId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ServiceAssignResponse> getByService(String serviceId) {
        return serviceAssignRepo.findAllByService_Id(serviceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ServiceAssignResponse create(ServiceAssignRequest req) {
        StudioAssign studioAssign = studioAssignRepo.findById(req.getStudioAssignId())
                .orElseThrow(() -> new AccountException("StudioAssign not found with id: " + req.getStudioAssignId()));
        Service service = (Service) serviceRepo.findById(req.getServiceId())
                .orElseThrow(() -> new AccountException("Service not found with id: " + req.getServiceId()));

        ServiceAssign serviceAssign = ServiceAssign.builder()
                .studioAssign(studioAssign)
                .service((com.studio.booking.entities.Service) service)
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build();

        serviceAssignRepo.save(serviceAssign);
        return toResponse(serviceAssign);
    }

    @Override
    public ServiceAssignResponse update(String id, ServiceAssignRequest req) {
        ServiceAssign serviceAssign = serviceAssignRepo.findById(id)
                .orElseThrow(() -> new AccountException("ServiceAssign not found with id: " + id));

        if (req.getIsActive() != null) {
            serviceAssign.setIsActive(req.getIsActive());
        }
        if (req.getServiceId() != null) {
            Service service = (Service) serviceRepo.findById(req.getServiceId())
                    .orElseThrow(() -> new AccountException("Service not found with id: " + req.getServiceId()));
            serviceAssign.setService((com.studio.booking.entities.Service) service);
        }
        if (req.getStudioAssignId() != null) {
            StudioAssign assign = studioAssignRepo.findById(req.getStudioAssignId())
                    .orElseThrow(() -> new AccountException("StudioAssign not found with id: " + req.getStudioAssignId()));
            serviceAssign.setStudioAssign(assign);
        }

        serviceAssignRepo.save(serviceAssign);
        return toResponse(serviceAssign);
    }

    @Override
    public String delete(String id) {
        ServiceAssign serviceAssign = serviceAssignRepo.findById(id)
                .orElseThrow(() -> new AccountException("ServiceAssign not found with id: " + id));
        serviceAssign.setIsActive(false);
        serviceAssignRepo.save(serviceAssign);
        return "ServiceAssign marked as inactive successfully!";
    }

    private ServiceAssignResponse toResponse(ServiceAssign sa) {
        return ServiceAssignResponse.builder()
                .id(sa.getId())
                .studioAssignId(sa.getStudioAssign() != null ? sa.getStudioAssign().getId() : null)
                .serviceId(sa.getService() != null ? sa.getService().getId() : null)
                .serviceName(sa.getService() != null ? sa.getService().getServiceName() : null)
                .serviceFee(sa.getService() != null ? sa.getService().getServiceFee() : null)
                .isActive(sa.getIsActive())
                .build();
    }
}
