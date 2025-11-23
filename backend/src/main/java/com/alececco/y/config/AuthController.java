package com.alececco.y.config;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    void register(@RequestBody CreateUserDTO createUserDTO) {
        userService.register(createUserDTO);
    }

}
