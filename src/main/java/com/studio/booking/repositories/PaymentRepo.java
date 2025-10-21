package com.studio.booking.repositories;

import com.studio.booking.entities.Payment;
import com.studio.booking.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, String> {
    List<Payment> findAllByStatusNot(PaymentStatus status);
    Optional<Payment> findTopByBooking_IdOrderByPaymentDateDesc(String bookingId);
}
