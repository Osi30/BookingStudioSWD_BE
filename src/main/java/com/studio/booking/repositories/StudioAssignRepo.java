package com.studio.booking.repositories;

import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudioAssignRepo extends JpaRepository<StudioAssign, String> {
    List<StudioAssign> findAllByStatusNot(AssignStatus status);
    List<StudioAssign> findAllByBooking_Id(String bookingId);
    List<StudioAssign> findAllByStudio_Id(String studioId);
}
