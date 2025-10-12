package com.studio.booking.dtos.response;

import com.studio.booking.enums.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {
    private String id;
    private String serviceName;
    private Double serviceFee;
    private String description;
    private ServiceStatus status;
}
