package com.studio.booking;

import com.studio.booking.utils.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BookingApplication {

	public static void main(String[] args) {
		DotenvLoader.loadEnv();
		SpringApplication.run(BookingApplication.class, args);
	}

}
