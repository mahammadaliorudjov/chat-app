package com.example.chatapp.user;

import com.example.chatapp.payload.PingMessage;
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
    public String addUser(@RequestParam String username, @RequestParam String password, Model model) {
        String url = userService.addUser(username, password, model);
        return url;
    }

    @MessageMapping("/user.ping")
    public void processUserPing(@Payload PingMessage pingMessage) {
        sessionService.updateLastPing(pingMessage.getSessionId());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findOnlineUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.findUsers());
    }

    @GetMapping("/getUser")
    public User getUser(String username) {
        return userRepository.findById(username).get();
    }
}