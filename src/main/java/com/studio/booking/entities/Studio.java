package com.studio.booking.entities;

import com.studio.booking.enums.StudioStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "studio")
public class Studio {
    @Id
    @Column(name = "studio_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "studio_name")
    private String studioName;

    @Column(name = "description")
    private String description;

    @Column(name = "area")
    private String area;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "status")
    private StudioStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "studio_type_id", referencedColumnName = "studio_type_id")
    private StudioType studioType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    private Location location;
}
