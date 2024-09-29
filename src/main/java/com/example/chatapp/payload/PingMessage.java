package com.example.chatapp.payload;

import lombok.Data;

@Data
public class PingMessage {
    private String nickname;
    private String sessionId;
}
