package com.studio.booking.ai;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GeminiClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${ai.gemini.apiKey}")
    private String apiKey;

    @Value("${ai.gemini.model}")
    private String model;

    @Value("${ai.gemini.baseUrl}")
    private String baseUrl;

    public Mono<String> generateResponse(String prompt) {
        String requestBody = """
                    {
                      "contents": [{
                        "role": "user",
                        "parts": [{"text": "%s"}]
                      }]
                    }
                """.formatted(prompt.replace("\"", "\\\""));

        return webClientBuilder.build()
                .post()
                .uri(baseUrl + "/models/" + model + ":generateContent?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractText);
    }

    private String extractText(String rawResponse) {
        // Parse đơn giản: tìm trường "text": "..."
        try {
            int i = rawResponse.indexOf("\"text\":");
            if (i == -1) return "[Empty response]";
            int start = rawResponse.indexOf('"', i + 7) + 1;
            int end = rawResponse.indexOf('"', start);
            return rawResponse.substring(start, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
        } catch (Exception e) {
            return "[Parse error or unexpected format]";
        }
    }
}
