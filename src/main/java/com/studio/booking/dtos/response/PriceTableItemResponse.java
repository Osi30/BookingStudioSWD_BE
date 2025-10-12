package com.studio.booking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTableItemResponse {
    private String id;
    private String priceTableId;
    private String studioTypeName;
    private Double defaultPrice;
}
