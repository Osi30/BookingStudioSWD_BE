package com.studio.booking.configs;

import com.studio.booking.repositories.PriceTableRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class ScheduleConfig {
    private final PriceTableRepo priceTableRepo;

    @Scheduled(cron = "0 0 1 * * *")
//    @Scheduled(cron = "0 * * * * *")
    public void UpdatePriceTableStatus() {
        int changeTablesReady = priceTableRepo.updateStatusIsReadyNow(LocalDate.now());
        System.out.println("Changes Table Is Happening Today: " + changeTablesReady);

        int changeTablesEnd = priceTableRepo.updateStatusIsEndNow(LocalDate.now().minusDays(1));
        System.out.println("Changes Table Is Ended Today: " + changeTablesEnd);
    }
}
