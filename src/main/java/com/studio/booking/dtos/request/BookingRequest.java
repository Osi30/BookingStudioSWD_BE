package com.studio.booking.dtos.request;

import com.studio.booking.enums.BookingType;

import com.studio.booking.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    private String studioTypeId;
    private String locationId;
    private String note;
    private String phoneNumber;
    private BookingType bookingType;
    private PaymentMethod paymentMethod;
    private List<StudioAssignRequest> studioAssignRequests;
}
