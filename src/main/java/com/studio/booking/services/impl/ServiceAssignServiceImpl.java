package com.studio.booking.services.impl;

import com.studio.booking.dtos.request.ServiceAssignRequest;
import com.studio.booking.dtos.response.ServiceAssignResponse;
import com.studio.booking.entities.Booking;
import com.studio.booking.entities.Service;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import com.studio.booking.enums.BookingType;
import com.studio.booking.enums.ServiceAssignStatus;
import com.studio.booking.enums.ServiceStatus;
import com.studio.booking.exceptions.exceptions.BookingException;
import com.studio.booking.repositories.ServiceAssignRepo;
import com.studio.booking.repositories.ServiceRepo;
import com.studio.booking.repositories.StudioAssignRepo;
import com.studio.booking.services.ServiceAssignService;
import lombok.RequiredArgsConstructor;
import com.studio.booking.exceptions.exceptions.ServiceException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
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
    public ServiceAssign create(ServiceAssignRequest req) {
        Service service = serviceRepo.findById(req.getServiceId())
                .orElseThrow(() -> new ServiceException("Service not found with id: " + req.getServiceId()));
        if (!service.getStatus().equals(ServiceStatus.AVAILABLE)) {
            throw new ServiceException("Service is not available with id: " + req.getServiceId());
        }

        return ServiceAssign.builder()
                .service(service)
                .status(ServiceAssignStatus.ACTIVE)
                .build();
    }

    @Override
    public List<ServiceAssign> createByList(List<String> serviceIds) {
        List<ServiceAssign> serviceAssigns = new ArrayList<>();

        List<Service> services = serviceRepo.findAllByIdIsInAndStatusIs(serviceIds, ServiceStatus.AVAILABLE);
        if (services.size() != serviceIds.size()) {
            throw new BookingException("There are less services available");
        }

        for (Service service : services) {
            serviceAssigns.add(ServiceAssign.builder()
                    .service(service)
                    .status(ServiceAssignStatus.ACTIVE)
                    .build());
        }

        return serviceAssigns;
    }

    @Override
    public ServiceAssignResponse update(String id, ServiceAssignRequest req) {
        ServiceAssign serviceAssign = serviceAssignRepo.findById(id)
                .orElseThrow(() -> new ServiceException("ServiceAssign not found with id: " + id));

        if (req.getServiceId() != null) {
            Service service = serviceRepo.findById(req.getServiceId())
                    .orElseThrow(() -> new ServiceException("Service not found with id: " + req.getServiceId()));
            serviceAssign.setService(service);
        }
        if (req.getStudioAssignId() != null) {
            StudioAssign assign = studioAssignRepo.findById(req.getStudioAssignId())
                    .orElseThrow(() -> new ServiceException("StudioAssign not found with id: " + req.getStudioAssignId()));
            serviceAssign.setStudioAssign(assign);
        }

        serviceAssignRepo.save(serviceAssign);
        return toResponse(serviceAssign);
    }

    @Override
    public String delete(String id) {
        ServiceAssign serviceAssign = serviceAssignRepo.findById(id)
                .orElseThrow(() -> new ServiceException("ServiceAssign not found with id: " + id));

        long dayBetween = ChronoUnit.DAYS.between(LocalDate.now(), serviceAssign.getStudioAssign().getStartTime());
        if (dayBetween <= 2) {
            throw new BookingException("Cannot update the data within two days before the start time of booking");
        }

        Double updatedAmount = serviceAssign.getService().getServiceFee();

        // Update amount in assign
        StudioAssign assign = serviceAssign.getStudioAssign();
        assign.setServiceAmount(assign.getServiceAmount() - updatedAmount);

        // Update amount in booking
        Booking booking = assign.getBooking();
        booking.setTotal(booking.getTotal() - updatedAmount);

        serviceAssign.setStatus(booking.getBookingType().equals(BookingType.PAY_FULL)
                ? ServiceAssignStatus.AWAITING_REFUND : ServiceAssignStatus.CANCELLED);

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
                .status(sa.getStatus())
                .build();
    }
}
