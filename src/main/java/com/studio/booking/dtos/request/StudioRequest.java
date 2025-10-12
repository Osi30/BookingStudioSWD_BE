package com.studio.booking.dtos.request;

import com.studio.booking.enums.StudioStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudioRequest {
    private String studioName;
    private String description;
    private String area;
    private LocalTime startTime;
    private LocalTime endTime;
    private StudioStatus status;
    private String locationId;
    private String studioTypeId;
}
