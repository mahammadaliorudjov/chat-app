package com.example.chatapp.session;

import com.example.chatapp.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByUser(User user);
    Session findBySessionId(String sessionId);
}
