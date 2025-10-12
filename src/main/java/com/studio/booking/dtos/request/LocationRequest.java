package com.studio.booking.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequest {
    private String locationName;
    private String address;
    private String contactNumber;
    private String longitude;
    private String latitude;
}
