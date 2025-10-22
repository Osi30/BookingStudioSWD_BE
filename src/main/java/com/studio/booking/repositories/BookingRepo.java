package com.studio.booking.repositories;

import com.studio.booking.entities.Booking;
import com.studio.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, String> {
    List<Booking> findAllByStatusNot(BookingStatus status);

//    @Query("""
//       select b
//       from Booking b
//       where b.locationId = :locationId
//       """)
//    List<Booking> findAllByLocationId(@Param("locationId") String locationId);
    @Query("""
           select distinct b
           from Booking b
           join b.studioAssigns sa
           join sa.studio s
           join s.location l
           where l.id = :locationId
           """)
    List<Booking> findAllByLocationId(@Param("locationId") String locationId);
    Booking findBookingById(String id);

    List<Booking> findAllByAccount_Id(String account);

}
