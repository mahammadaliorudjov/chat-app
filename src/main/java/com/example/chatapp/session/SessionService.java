package com.example.chatapp.session;

import com.example.chatapp.user.Status;
import com.example.chatapp.user.User;
import com.example.chatapp.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public boolean userIsOnline(User user) {
        List<Session> sessions = sessionRepository.findByUser(user);
        return !sessions.isEmpty();
    }

    @Transactional
    public void addSession(String sessionId, String username) {
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUser(userRepository.findById(username).get());
        session.setLastPingTime(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Transactional
    public void deleteSessions() {
        sessionRepository.deleteAll();
    }

    @Transactional
    public void deleteSession(String sessionId) {
        Session session = sessionRepository.findBySessionId(sessionId);
        if (session != null) {
            sessionRepository.delete(session);
            System.out.println("Session with ID " + sessionId + " deleted.");
        } else {
            System.out.println("Session with ID " + sessionId + " not found.");
        }
    }
    @Transactional
    public void updateLastPing(String sessionId) {
        Session session = sessionRepository.findBySessionId(sessionId);
        if (session != null) {
            session.updateLastPingTime();
            sessionRepository.save(session);
        }
    }
    @Scheduled(fixedRate = 5000)
    public void checkInactiveSessions() {
        List<Session> sessions = sessionRepository.findAll();
        for (Session session : sessions) {
            if (session.getLastPingTime().isBefore(LocalDateTime.now().minusSeconds(10))) {
                User user = session.getUser();
                sessionRepository.delete(session);
                if(!userIsOnline(user)) {
                    user.setStatus(Status.OFFLINE);
                    userRepository.save(user);
                }
                messagingTemplate.convertAndSend("/topic/user-status", "refresh");
            }
        }
    }
}