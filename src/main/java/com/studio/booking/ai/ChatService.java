package com.studio.booking.ai;

import com.studio.booking.enums.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final GeminiClient geminiClient;
    private final IntentDetector intentDetector;
    private final ChatDataProvider chatDataProvider;

    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý AI của hệ thống Booking Studio.
        Hãy trả lời thân thiện, rõ ràng, ngắn gọn và chỉ dựa trên thông tin thật bên dưới.
        Nếu không tìm thấy dữ liệu liên quan, hãy nói "Xin lỗi, tôi không có thông tin đó."
        """;

    public Mono<String> chat(String sessionId, String userMessage) {
        ChatSession session = sessions.computeIfAbsent(sessionId, k -> new ChatSession());
        session.addMessage("User", userMessage);

        return intentDetector.detectIntent(userMessage)
                .flatMap(intent -> handleIntent(session, intent, userMessage));
    }

    private Mono<String> handleIntent(ChatSession session, IntentType intent, String userMessage) {
        String contextData = switch (intent) {
            case LOCATION_INFO -> chatDataProvider.getAllLocations();
            case SERVICE_INFO -> chatDataProvider.getServiceList();
            case PRICE_INQUIRY -> chatDataProvider.getBasicPriceInfo();
            case OPENING_HOURS -> chatDataProvider.getOpeningHours();
            case BOOKING_INQUIRY -> "Người dùng muốn đặt studio. Bạn có thể hướng dẫn họ truy cập trang Đặt lịch hoặc cung cấp thông tin cần thiết.";
            // ✅ thêm mới:
            case STUDIO_INFO -> chatDataProvider.getStudioList();
            // 👇 fallback
            default -> "";
        };

        String finalPrompt = """
        %s

        Dữ liệu thật của hệ thống:
        %s

        Ngữ cảnh hội thoại:
        %s

        Câu hỏi người dùng: "%s"

        Trả lời thân thiện, có dẫn chứng từ dữ liệu trên.
        """.formatted(SYSTEM_PROMPT, contextData, session.getContext(), userMessage);

        return geminiClient.generateResponse(finalPrompt)
                .map(reply -> {
                    session.addMessage("AI", reply);
                    return "[Intent: " + intent.name() + "] " + reply;
                });
    }

    public void resetSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
