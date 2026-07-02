package com.doodle.service;

import com.doodle.dto.request.CreateUserRequest;
import com.doodle.dto.response.UserResponse;
import com.doodle.entity.User;
import com.doodle.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {

        CreateUserRequest request = new CreateUserRequest();
        request.setName("John");
        request.setEmail("john@test.com");

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("john@test.com", response.getEmail());

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void shouldReturnUserById() {

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponse response = userService.getById(1L);

        assertEquals(1L, response.getId());
        assertEquals("John", response.getName());
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getById(1L));
    }

    @Test
    void shouldReturnAllUsers() {

        List<User> users = List.of(
                User.builder().id(1L).name("A").email("a@test.com").build(),
                User.builder().id(2L).name("B").email("b@test.com").build()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
    }
}