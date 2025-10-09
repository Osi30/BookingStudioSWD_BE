package com.studio.booking.dtos.request;

import com.studio.booking.enums.AssignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminStudioAssignRequest {
    private String bookingId;
    private String studioId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double studioAmount;
    private Double serviceAmount;
    private Double additionTime;
    private AssignStatus status;
}
