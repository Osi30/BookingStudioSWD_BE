package com.studio.booking.entities;

import com.studio.booking.enums.PriceTableStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "price_table")
public class PriceTable {
    @Id
    @Column(name = "price_table_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private PriceTableStatus status;

    @Column(name = "priority")
    private Integer priority;
}
