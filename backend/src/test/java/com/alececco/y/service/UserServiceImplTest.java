package com.alececco.y.service;

import com.alececco.y.dto.CreateUserDTO;
import com.alececco.y.models.UserRole;
import com.alececco.y.models.Users;
import com.alececco.y.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_nullDto_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.register(null));
        assertEquals("CreateUserDTO cannot be null", ex.getMessage());
        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void register_validDto_encodesPasswordAndSavesUser() {
        // Arrange
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("alice");
        dto.setRawPassword("secret");
        dto.setRole(UserRole.USER);

        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");

        // capture the user passed to save and return it
        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        when(userRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Users saved = userService.register(dto);

        // Assert
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(Users.class));

        Users captured = captor.getValue();
        assertNotNull(captured);
        assertEquals("alice", captured.getUsername());
        assertEquals("encoded-secret", captured.getPassword());
        assertEquals(UserRole.USER, captured.getRole());

        // ensure returned user is the same as saved by repository
        assertSame(captured, saved);
    }

    @Test
    void getAll_delegatesToRepository() {
        // Arrange
        Users u1 = Users.builder().username("u1").password("p1").role(UserRole.USER).build();
        Users u2 = Users.builder().username("u2").password("p2").role(UserRole.ADMIN).build();
        List<Users> expected = Arrays.asList(u1, u2);
        when(userRepository.findAll()).thenReturn(expected);

        // Act
        List<Users> result = userService.getAll();

        // Assert
        verify(userRepository).findAll();
        assertEquals(expected, result);
    }
}