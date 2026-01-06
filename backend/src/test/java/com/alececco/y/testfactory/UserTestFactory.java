package com.alececco.y.testfactory;

import com.alececco.y.models.UserRole;
import com.alececco.y.models.Users;

public class UserTestFactory {

    public static Users validUser() {
        return Users.builder()
                .id(1L)
                .username("test-user")
                .role(UserRole.USER)
                .build();
    }

}
