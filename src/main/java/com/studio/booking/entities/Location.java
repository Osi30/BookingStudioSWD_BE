package com.studio.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "location")
public class Location {
    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @JsonIgnore
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    private List<Account> accounts;
}
