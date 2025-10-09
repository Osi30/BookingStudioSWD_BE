package com.studio.booking.dtos.request;

import com.studio.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBookingStatusRequest {
    private BookingStatus status;
    private String note; // Ghi chú nếu cần (khi cancel)
}
