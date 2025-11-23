package com.alececco.y.config;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.alececco.y.models.Users;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()));

            Users user = (Users) authentication.getPrincipal();

            String token = jwtService.generateToken(user);

            return new AuthenticationResponse(token);
        } catch (AuthenticationException ae) {
            System.out.println("Invalid credentials: " + ae.getMessage());
            return new AuthenticationResponse("Invalid username or password");
        }
    }
}
