package com.studio.booking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceAssignResponse {
    private String id;
    private String studioAssignId;
    private String serviceId;
    private String serviceName;
    private Double serviceFee;
    private Boolean isActive;
}
