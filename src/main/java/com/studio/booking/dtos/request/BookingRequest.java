package com.studio.booking.dtos.request;

import com.studio.booking.enums.BookingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    private String note;
    private Double total;
    private BookingType bookingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int studioQuantity;
    private String studioTypeId;
    private Map<String, List<String>> studioServicesId;
}
