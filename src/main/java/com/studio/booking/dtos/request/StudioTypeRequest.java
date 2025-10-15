package com.studio.booking.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudioTypeRequest {
    private String name;
    private String description;
    private Double minArea;
    private Double maxArea;
    private Double bufferTime;
    private List<String> serviceIds; // Dịch vụ sẽ gán
}
