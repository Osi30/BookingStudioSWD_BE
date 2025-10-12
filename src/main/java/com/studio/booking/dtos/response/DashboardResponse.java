package com.studio.booking.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private Long totalAccounts;
    private Long totalStudios;
    private Long totalBookings;
    private Long totalPayments;
    private Double totalRevenue;

    private Map<String, Long> bookingStatusStats; // IN_PROGRESS: 10, COMPLETED: 5, CANCELLED: 2
    private Map<String, Double> revenueByMonth;   // "2025-09": 1000000.0

    private String topServiceName;
    private Long topServiceUsage;
}
