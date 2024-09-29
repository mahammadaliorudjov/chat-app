package com.example.chatapp.user;

import com.example.chatapp.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserStatusController {
    private final SimpMessagingTemplate messagingTemplate;
    private final SessionService sessionService;
    private final UserService userService;
    private final UserRepository userRepository;
    @MessageMapping("/user-status")
    public void processUserStatus(@Payload UserStatusMessage statusMessage) {
        if("offline".equals(statusMessage.getStatus())) {
            sessionService.deleteSession(statusMessage.getSessionId());
            if(!sessionService.userIsOnline(userRepository.findById(statusMessage.getUsername()).get())) {
                userService.disconnect(userRepository.findById(statusMessage.getUsername()).get());
            }
        }
        messagingTemplate.convertAndSend("/topic/user-status", "refresh");
    }
}
