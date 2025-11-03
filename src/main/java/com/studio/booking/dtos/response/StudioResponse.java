package com.studio.booking.dtos.response;

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
public class StudioResponse {
    private String id;
    private String studioName;
    private String description;
    private Double acreage;
    private LocalTime startTime;
    private LocalTime endTime;
    private String imageUrl;
    private StudioStatus status;
    private String locationName;
    private String studioTypeName;
    private String studioTypeId;
}
