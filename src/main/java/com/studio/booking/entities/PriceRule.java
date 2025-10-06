package com.studio.booking.entities;

import com.studio.booking.enums.PriceUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "price_rule")
public class PriceRule {
    @Id
    @Column(name = "price_rule_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "day_filter")
    private Integer dayFilter;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "price_per_unit")
    private Double pricePerUnit;

    @Column(name = "unit")
    private PriceUnit unit;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "price_table_item_id", referencedColumnName = "price_table_item_id")
    private PriceTableItem priceTableItem;
}
