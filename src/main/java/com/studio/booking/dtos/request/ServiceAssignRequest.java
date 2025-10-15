package com.studio.booking.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceAssignRequest {
    private String studioAssignId;
    private String serviceId;
    private Boolean isActive;
}
