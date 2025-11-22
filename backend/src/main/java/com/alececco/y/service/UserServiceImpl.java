package com.alececco.y.service;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.models.Users;
import com.alececco.y.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Users register(CreateUserDTO createUserDTO) {
        Users u = Users.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getRawPassword()))
                .build();

        return userRepository.save(u);
    }

    @Override
    public List<Users> getAll() {
        return userRepository.findAll();
    }
}
