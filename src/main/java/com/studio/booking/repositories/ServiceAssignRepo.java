package com.studio.booking.repositories;

import com.studio.booking.entities.ServiceAssign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceAssignRepo extends JpaRepository<ServiceAssign, String> {
    List<ServiceAssign> findAllByStudioAssign_Id(String studioAssignId);
    List<ServiceAssign> findAllByService_Id(String serviceId);
}
