package com.studio.booking.ai;

import com.studio.booking.enums.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class IntentDetector {
    private final GeminiClient geminiClient;

    public Mono<IntentType> detectIntent(String message) {
        String prompt = """
                Phân loại câu hỏi của người dùng vào một trong các nhóm sau:
                1. BOOKING_INQUIRY - nếu người dùng hỏi hoặc muốn đặt lịch, đặt phòng, đặt studio.
                2. PRICE_INQUIRY - nếu hỏi về giá, chi phí, tiền thuê.
                3. OPENING_HOURS - nếu hỏi về thời gian mở cửa, giờ hoạt động.
                4. LOCATION_INFO - nếu hỏi về địa chỉ, chi nhánh, vị trí.
                5. SERVICE_INFO - nếu hỏi về dịch vụ cụ thể hoặc chi tiết gói chụp.
                6. GENERAL_CHAT - nếu là lời chào, cảm ơn, hoặc không thuộc nhóm nào trên.
                
                Câu hỏi: "%s"
                
                Chỉ trả về tên nhóm (ví dụ: PRICE_INQUIRY).
                """.formatted(message);

        return geminiClient.generateResponse(prompt)
                .map(resp -> {
                    String clean = resp.trim().toUpperCase();
                    for (IntentType type : IntentType.values()) {
                        if (clean.contains(type.name())) {
                            return type;
                        }
                    }
                    return IntentType.GENERAL_CHAT;
                });
    }
}

