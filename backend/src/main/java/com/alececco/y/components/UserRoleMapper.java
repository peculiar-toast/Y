package com.alececco.y.components;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import com.alececco.y.dto.UserRoleDTO;
import com.alececco.y.models.UserRole;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UserRoleMapper {
    private final ModelMapper modelMapper;
    
    public UserRoleMapper() {
        this.modelMapper = new ModelMapper();
    }

    public UserRole toUserRole(UserRoleDTO userRoleDTO) {
        return modelMapper.map(userRoleDTO, UserRole.class);
    }
}
