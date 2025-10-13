package com.studio.booking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "price_table_item")
public class PriceTableItem {
    @Id
    @Column(name = "price_table_item_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "default_price")
    private Double defaultPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "price_table_id", referencedColumnName = "price_table_id")
    private PriceTable priceTable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "studio_type_id", referencedColumnName = "studio_type_id")
    private StudioType studioType;

    @OneToMany(mappedBy = "priceTableItem", fetch = FetchType.LAZY)
    private List<PriceRule> rules;
}
