package com.studio.booking.ai;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GeminiClient {
    private final WebClient webClient;
    private final String apiKey;
    private final String modelName;
    private final String baseUrl;

    public GeminiClient(
            @Value("${ai.gemini.apiKey}") String apiKey,
            @Value("${ai.gemini.model}") String modelName,
            @Value("${ai.gemini.baseUrl}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<String> generateResponse(String prompt) {
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);
        parts.add(textPart);

        JsonArray contents = new JsonArray();
        JsonObject contentItem = new JsonObject();
        contentItem.add("parts", parts);
        contents.add(contentItem);
        content.add("contents", contents);

        // 🔹 API mới yêu cầu thêm “-latest”
        String modelPath = String.format("/models/%s:generateContent", modelName);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(modelPath)
                        .queryParam("key", apiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(content.toString())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractTextFromResponse)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just("⚠️ Lỗi khi gọi Gemini API: " + e.getMessage());
                });
    }

    private String extractTextFromResponse(String responseBody) {
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        try {
            return json.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        } catch (Exception e) {
            return "(No response)";
        }
    }

    // Stream method
    public Flux<String> generateResponseStream(String prompt) {
        return generateResponse(prompt)
                .flatMapMany(resp -> Flux.fromArray(resp.split(" ")))
                .delayElements(java.time.Duration.ofMillis(100)); // mô phỏng typing effect
    }

}
