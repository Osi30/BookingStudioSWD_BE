package com.studio.booking.dtos.response;

import com.studio.booking.enums.BookingStatus;
import com.studio.booking.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private String id;
    private LocalDateTime bookingDate;
    private LocalDateTime updateDate;
    private String note;
    private Double total;
    private BookingStatus status;
    private BookingType bookingType;

    // Related info
    private String accountEmail;
    private String accountName;
    private String studioTypeName;
}
