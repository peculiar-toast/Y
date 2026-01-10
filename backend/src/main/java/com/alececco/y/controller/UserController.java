package com.alececco.y.controller;

import com.alececco.y.dto.UserDTO;
import com.alececco.y.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
    }

}
