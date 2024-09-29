package com.example.chatapp.user;

import lombok.Data;

@Data
public class UserStatusMessage {
    private String username;
    private String status;
    private String sessionId;
}
