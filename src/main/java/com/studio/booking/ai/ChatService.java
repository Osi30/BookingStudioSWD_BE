package com.studio.booking.ai;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final GeminiClient geminiClient;

    // Giả lập session lưu trong RAM (key = userId hoặc "guest")
    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    // Prompt hệ thống để định hướng chatbot
    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý AI của hệ thống Booking Studio.
        Nhiệm vụ của bạn là:
        - Giải thích, tư vấn các gói dịch vụ, studio, thời gian, và quy trình đặt lịch.
        - Không cung cấp thông tin ngoài phạm vi dịch vụ của Booking Studio.
        - Trả lời thân thiện, chuyên nghiệp, ngắn gọn.
        Ví dụ: "Studio A hiện trống vào cuối tuần, bạn muốn tôi hỗ trợ đặt giúp không?"
        """;

    // 🧠 Thêm tham số userMessage
    public Mono<String> chat(String sessionId, String userMessage) {
        ChatSession session = sessions.computeIfAbsent(sessionId, k -> new ChatSession());

        // Ghi lại tin nhắn người dùng
        session.addMessage("User", userMessage);

        // Gộp ngữ cảnh + prompt
        String fullPrompt = SYSTEM_PROMPT + "\n\n" + session.getContext();

        return geminiClient.generateResponse(fullPrompt)
                .map(reply -> {
                    session.addMessage("AI", reply);
                    return reply;
                });
    }

    // Reset hội thoại nếu cần
    public void resetSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
