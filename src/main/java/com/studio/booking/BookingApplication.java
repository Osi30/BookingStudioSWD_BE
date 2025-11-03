package com.studio.booking;

import com.studio.booking.utils.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableJpaRepositories(basePackages = "com.studio.booking.repositories")
public class BookingApplication {

	public static void main(String[] args) {
		DotenvLoader.loadEnv();
		SpringApplication.run(BookingApplication.class, args);
	}

}
