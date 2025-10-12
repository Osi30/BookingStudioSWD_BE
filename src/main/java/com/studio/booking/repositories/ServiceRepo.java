package com.studio.booking.repositories;

import com.studio.booking.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.studio.booking.entities.Service;

import java.util.List;

public interface ServiceRepo extends JpaRepository<Service, String> {
    List<Service> findAllByStatusNot(ServiceStatus status);
}
