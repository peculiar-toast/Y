package com.alececco.y.dto;

import com.alececco.y.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    String username;
    String rawPassword;
    UserRole role;
}
