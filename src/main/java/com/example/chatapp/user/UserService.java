package com.example.chatapp.user;

import com.example.chatapp.session.SessionService;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    @Transactional
    public String addUser(String username, String password, Model model){
        Optional<User> op = userRepository.findById(username);
        if(op.isPresent()) {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "registration.html";
        }
        else {
            User user = new User();
            user.setUsername(username);
            user.setRole(Roles.USER);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(Status.OFFLINE);
            userRepository.save(user);
            return "redirect:/login";
        }
    }
    @Transactional
    public void disconnect(User user){
        User disconnectedUser = userRepository.findById(user.getUsername()).orElse(null);
        if (disconnectedUser != null) {
            disconnectedUser.setStatus(Status.OFFLINE);
            userRepository.save(disconnectedUser);
        }
    }
    public String findUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return username;
    }
    @PreDestroy
    @Transactional
    public void onShutdown() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setStatus(Status.OFFLINE);
            userRepository.save(user);
        }
        sessionService.deleteSessions();
    }
    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public List<User> findUsers() {
        return userRepository.findAll();
    }

    public void turnOnline(String username) {
        User user = userRepository.findById(username).get();
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }
}
