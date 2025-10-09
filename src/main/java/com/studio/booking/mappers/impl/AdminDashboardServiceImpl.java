package com.studio.booking.mappers.impl;

import com.studio.booking.dtos.response.AdminDashboardResponse;
import com.studio.booking.entities.Payment;
import com.studio.booking.entities.ServiceAssign;
import com.studio.booking.enums.BookingStatus;
import com.studio.booking.enums.PaymentStatus;
import com.studio.booking.repositories.*;
import com.studio.booking.services.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final AccountRepo accountRepo;
    private final StudioRepo studioRepo;
    private final BookingRepo bookingRepo;
    private final PaymentRepo paymentRepo;
    private final ServiceAssignRepo serviceAssignRepo;

    @Override
    public AdminDashboardResponse getDashboardOverview() {
        long totalAccounts = accountRepo.count();
        long totalStudios = studioRepo.count();
        long totalBookings = bookingRepo.count();
        long totalPayments = paymentRepo.count();

        double totalRevenue = paymentRepo.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();

        // Thống kê booking theo status
        Map<String, Long> bookingStatusStats = Arrays.stream(BookingStatus.values())
                .collect(Collectors.toMap(Enum::name,
                        status -> bookingRepo.findAll().stream()
                                .filter(b -> b.getStatus() == status)
                                .count()));

        // Doanh thu theo tháng (YYYY-MM)
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Double> revenueByMonth = paymentRepo.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS && p.getPaymentDate() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentDate().format(fmt),
                        Collectors.summingDouble(Payment::getAmount)
                ));

        // Tính top service
        Map<String, Long> serviceUsage = serviceAssignRepo.findAll().stream()
                .filter(ServiceAssign::getIsActive)
                .collect(Collectors.groupingBy(
                        sa -> sa.getService().getServiceName(),
                        Collectors.counting()
                ));

        String topServiceName = "N/A";
        Long topUsage = 0L;
        if (!serviceUsage.isEmpty()) {
            Map.Entry<String, Long> top = serviceUsage.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            if (top != null) {
                topServiceName = top.getKey();
                topUsage = top.getValue();
            }
        }

        return AdminDashboardResponse.builder()
                .totalAccounts(totalAccounts)
                .totalStudios(totalStudios)
                .totalBookings(totalBookings)
                .totalPayments(totalPayments)
                .totalRevenue(totalRevenue)
                .bookingStatusStats(bookingStatusStats)
                .revenueByMonth(revenueByMonth)
                .topServiceName(topServiceName)
                .topServiceUsage(topUsage)
                .build();
    }
}
