package com.studio.booking.entities;

import com.studio.booking.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.codec.StringDecoder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "service")
public class Service {
    @Id
    @Column(name = "service_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "service_fee")
    private Double serviceFee;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private ServiceStatus status;
}
