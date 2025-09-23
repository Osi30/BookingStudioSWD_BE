package com.studio.booking.entities;

import com.studio.booking.utils.GenerateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "addition_time")
public class AdditionTime {
    @Id
    @Column(name = "addition_time_id", length = 10)
    private String id;

    @Column(name = "time")
    private Integer time;

    @Column(name = "amount")
    private Double amount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
    private Booking booking;

    @PrePersist
    public void generateId() {
        this.id = GenerateUtil.generateRandomWords(10);
    }
}
