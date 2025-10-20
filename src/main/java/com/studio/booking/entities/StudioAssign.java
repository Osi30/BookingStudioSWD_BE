package com.studio.booking.entities;

import com.studio.booking.enums.AssignStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "studio_assign")
public class StudioAssign {
    @Id
    @Column(name = "studio_assign_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "studio_amount")
    private Double studioAmount;

    @Column(name = "service_amount")
    private Double serviceAmount;

    @Column(name = "addition_time")
    private Double additionTime;

    @Column(name = "status")
    private AssignStatus status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "studio_id", referencedColumnName = "studio_id")
    private Studio studio;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
    private Booking booking;

    @OneToMany(mappedBy = "studioAssign", cascade = CascadeType.ALL)
    private List<ServiceAssign> serviceAssigns;
}
