package com.alececco.y.service;

import java.util.List;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.models.Users;

public interface UserService {
    Users register(CreateUserDTO createUserDTO);

    List<Users> getAll();
}
