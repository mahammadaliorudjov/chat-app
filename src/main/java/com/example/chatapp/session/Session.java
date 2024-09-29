package com.example.chatapp.session;

import com.example.chatapp.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private User user;
    private String sessionId;
    private LocalDateTime lastPingTime;
    public void updateLastPingTime() {
        this.lastPingTime = LocalDateTime.now();
    }
}
