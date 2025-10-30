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

    @PostMapping("/send")
    public Mono<ResponseEntity<BaseResponse>> send(@RequestBody String message) {
        return chatService.chat(message)
                .map(response -> ResponseEntity.ok(
                        BaseResponse.builder()
                                .code(HttpStatus.OK.value())
                                .message("AI reply successfully!")
                                .data(response)
                                .build()
                ));
    }
}
