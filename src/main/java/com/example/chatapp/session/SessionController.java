package com.example.chatapp.session;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    @PostMapping("/setSessionId")
    public void setSessionId(@RequestHeader("X-Session-ID") String sessionId,
                             @RequestHeader("X-Username") String username) {
        sessionService.addSession(sessionId, username);
    }
}
