package com.example.chatapp.session;

import com.example.chatapp.user.User;
import com.example.chatapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    public boolean userIsOnline(User user) {
        List<Session> sessions = sessionRepository.findByUser(user);
        return !sessions.isEmpty();
    }

    public void addSession(String sessionId, String username) {
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUser(userRepository.findById(username).get());
        sessionRepository.save(session);
    }

    public void deleteSessions() {
        sessionRepository.deleteAll();
    }

    public void deleteSession(String sessionId) {
        sessionRepository.delete(sessionRepository.findBySessionId(sessionId));
    }
}
