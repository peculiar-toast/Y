package com.alececco.y.service;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.models.User;
import com.alececco.y.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(CreateUserDTO createUserDTO) {
        User u = User.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getRawPassword()))
                .build();

        return userRepository.save(u);
    }
}
