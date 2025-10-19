package com.studio.booking.repositories;

import com.studio.booking.entities.Booking;
import com.studio.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, String> {
    List<Booking> findAllByStatusNot(BookingStatus status);
    List<Booking> findAllByAccount_Id(String account);
}
