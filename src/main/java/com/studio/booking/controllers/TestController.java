package com.studio.booking.controllers;

import com.studio.booking.services.NotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final NotificationService notificationService;

    @GetMapping("/test-notification")
    public String testNotification() throws MessagingException {
        // CẦN THAY THẾ ID NÀY BẰNG ID CỦA BẠN SAU KHI ĐĂNG NHẬP
        String myAccountId = "GKYTDTZNMG";

        String title = "THÔNG BÁO KIỂM TRA!";
        String body = "Nếu bạn thấy thông báo này, hệ thống đã hoạt động.";

        // Dữ liệu bổ sung (data payload)
        Map<String, String> data = Map.of(
                "screen", "booking_detail",
                "booking_id", "FCSXGVFHZR"
        );

        // Kích hoạt gửi thông báo
        notificationService.sendNotificationToUser(myAccountId, title, body, data);

        return "Notification sent attempt initiated. Check your Flutter app now!";
    }
}
