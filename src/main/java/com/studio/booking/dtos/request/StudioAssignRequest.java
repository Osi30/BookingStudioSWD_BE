package com.studio.booking.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.studio.booking.enums.AssignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudioAssignRequest {
    private String bookingId;
    private String studioTypeId;
    private String locationId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    private Integer hour;

    private Double studioAmount;
    private Double serviceAmount;
    private Double additionTime;
    private Long bufferMinutes;
    private AssignStatus status;
    private List<String> serviceIds;
}
