package com.studio.booking.repositories;

import com.studio.booking.entities.Payment;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, String> {
    List<Payment> findAllByStatusNot(PaymentStatus status);
    Optional<Payment> findTopByBooking_IdOrderByPaymentDateDesc(String bookingId);
    List<Payment> findAllByBooking_IdOrderByPaymentDateDesc(String bookingId);

    Optional<Payment> findTopByBooking_IdAndPaymentTypeOrderByPaymentDateDesc(String bookingId, PaymentType type);

    @Query("""
           select coalesce(sum(p.amount), 0)
           from Payment p
           where p.booking.id = :bookingId
             and p.paymentType in :types
             and p.status = :status
           """)
    Double sumAmountByBookingAndTypesAndStatus(@Param("bookingId") String bookingId,
                                               @Param("types") Collection<PaymentType> types,
                                               @Param("status") PaymentStatus status);
}
