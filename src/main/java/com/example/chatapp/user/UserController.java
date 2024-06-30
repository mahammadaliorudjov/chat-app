package com.example.chatapp.user;

import com.example.chatapp.payload.DisconnectPayload;
import com.example.chatapp.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    @PostMapping("/user/add")
    public String addUser(@RequestParam String username, @RequestParam String password, Model model){
        String url = userService.addUser(username, password, model);
        return url;
    }
    @MessageMapping("/user.disconnectUser")
    public User disconnectUser(@Payload DisconnectPayload disconnectPayload) {
        String nickname = disconnectPayload.getNickname();
        String sessionId = disconnectPayload.getSessionId();
        sessionService.deleteSession(sessionId);
        User user = userRepository.findById(nickname).get();
        if(!sessionService.userIsOnline(user)) {
            userService.disconnect(user);
        }
        return user;
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> findOnlineUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.findUsers());
    }

}