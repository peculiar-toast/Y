package com.alececco.y.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordEncoder passwordEncoder;

    @RequestMapping("/public/pass")
    public String getPass(@RequestParam String raw) {
        return passwordEncoder.encode(raw);
    }

    @RequestMapping("/public/hello")
    public String helloEveryone() {
        System.out.println("CIOAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOo");
        return "Hello everyone!";
    }

    @RequestMapping("/secure/hello")
    public String secureHello() {
        return "Hello authenticated user!";
    }

    @RequestMapping("/user/hello")
    public String userHello() {
        return "HELLO USERRRR!";
    }

    @RequestMapping("/admin/hello")
    public String adminHello() {
        return "HELLO ADMINNNNN!";
    }
}
