package com.example.chatapp.payload;

import lombok.Data;

@Data
public class DisconnectPayload {
    private String nickname;
    private String sessionId;
}
