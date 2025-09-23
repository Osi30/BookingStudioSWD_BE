package com.studio.booking.entities;

import com.studio.booking.enums.BookingStatus;
import com.studio.booking.enums.BookingType;
import com.studio.booking.utils.GenerateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "booking_id", length = 10)
    private String id;

    @CreationTimestamp
    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Column(name = "note")
    private String note;

    @Column(name = "total")
    private Double total;

    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "booking_type")
    private BookingType bookingType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id")
    private Account account;

    @PrePersist
    public void generateId() {
        this.id = GenerateUtil.generateRandomWords(10);
    }
}
