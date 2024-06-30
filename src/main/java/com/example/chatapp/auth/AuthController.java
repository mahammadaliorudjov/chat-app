package com.example.chatapp.auth;

import com.example.chatapp.user.UserRepository;
import com.example.chatapp.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@SessionAttributes("username")
public class AuthController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/success")
    public String success(Model model) {
        String username = userService.findUser();
        userService.turnOnline(username);
        model.addAttribute("username", username);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(@SessionAttribute("username") String username, Model model) {
        model.addAttribute("username", username);
        return "index";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }
}
