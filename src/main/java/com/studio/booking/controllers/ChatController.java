package com.studio.booking.controllers;
import com.studio.booking.dtos.BaseResponse;
import com.studio.booking.ai.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // ✅ thêm sessionId vào param
    @PostMapping("/send")
    public Mono<ResponseEntity<BaseResponse>> send(
            @RequestParam(defaultValue = "guest") String sessionId,
            @RequestBody String message
    ) {
        return chatService.chat(sessionId, message)
                .map(response -> ResponseEntity.ok(
                        BaseResponse.builder()
                                .code(HttpStatus.OK.value())
                                .message("AI reply successfully!")
                                .data(response)
                                .build()
                ));
    }

    @PostMapping("/reset")
    public ResponseEntity<BaseResponse> reset(
            @RequestParam(defaultValue = "guest") String sessionId
    ) {
        chatService.resetSession(sessionId);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Chat session reset successfully!")
                .data(null)
                .build());
    }
}
