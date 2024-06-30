package com.example.chatapp.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Roles role;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Override
    public String toString() {
        return "User{" +
                "username=" + username + '}';
    }
}
