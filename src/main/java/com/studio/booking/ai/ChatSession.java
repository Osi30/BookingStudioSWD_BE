package com.studio.booking.ai;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatSession {
    private final List<String> history = new ArrayList<>();

    public void addMessage(String role, String message) {
        history.add(role + ": " + message);
    }

    public String getContext() {
        return String.join("\n", history);
    }
}
