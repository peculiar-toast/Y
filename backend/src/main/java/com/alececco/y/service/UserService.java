package com.alececco.y.service;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.models.Users;

public interface UserService {
    Users register(CreateUserDTO createUserDTO);
}
