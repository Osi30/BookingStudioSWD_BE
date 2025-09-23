package com.studio.booking.entities;

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
@Table(name = "service_assign")
public class ServiceAssign {
    @Id
    @Column(name = "service_assign_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "studio_assign_id", referencedColumnName = "studio_assign_id")
    private StudioAssign studioAssign;
}
