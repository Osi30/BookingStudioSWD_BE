package com.studio.booking.controllers;

import com.studio.booking.ai.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.time.Duration;

@RestController
@RequestMapping("/api/chat-stream")
@RequiredArgsConstructor
public class ChatStreamController {
    private final ChatService chatService;

    @PostMapping(value = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @RequestParam(defaultValue = "guest") String sessionId,
            @RequestBody String message
    ) {
        return chatService.chat(sessionId, message)
                .flatMapMany(resp -> Flux.fromArray(resp.split(" ")))
                .map(word -> word + " ")
                .delayElements(Duration.ofMillis(80)); // typing effect
    }
}
