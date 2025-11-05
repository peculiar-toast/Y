package com.alececco.y.service;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.models.User;

public interface UserService {
    User register(CreateUserDTO createUserDTO);
}
