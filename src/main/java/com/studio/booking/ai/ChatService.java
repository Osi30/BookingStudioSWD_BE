package com.studio.booking.ai;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final GeminiClient geminiClient;

    public Mono<String> chat(String userMessage) {
        return geminiClient.generateResponse(userMessage);
    }
}
