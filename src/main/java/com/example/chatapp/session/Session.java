package com.example.chatapp.session;

import com.example.chatapp.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    private User user;
    private String sessionId;
}
