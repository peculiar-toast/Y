package com.alececco.y.controller;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    void register(@RequestBody CreateUserDTO createUserDTO) {
        userService.register(createUserDTO);
    }
}
