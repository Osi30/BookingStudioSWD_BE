package com.studio.booking.repositories;

import com.studio.booking.entities.StudioAssign;
import com.studio.booking.enums.AssignStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

import java.util.List;

public interface StudioAssignRepo extends JpaRepository<StudioAssign, String> {
    List<StudioAssign> findAllByStatusNot(AssignStatus status);
    List<StudioAssign> findAllByBooking_Id(String bookingId);
    List<StudioAssign> findAllByStudio_Id(String studioId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sa from StudioAssign sa where sa.id = :id")
    Optional<StudioAssign> findByIdForUpdate(@Param("id") String id);
    boolean existsByBooking_IdAndStatusNot(String bookingId, AssignStatus status);
    @Query("""
       select coalesce(
                 sum(coalesce(sa.studioAmount, 0) + coalesce(sa.serviceAmount, 0))
             , 0)
       from StudioAssign sa
       where sa.booking.id = :bookingId
         and sa.status <> :excluded
       """)
    Double sumAmountsByBookingIdAndStatusNot(@Param("bookingId") String bookingId,
                                             @Param("excluded") com.studio.booking.enums.AssignStatus excluded);
}
