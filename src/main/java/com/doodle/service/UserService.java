package com.doodle.service;


import com.doodle.dto.request.CreateUserRequest;
import com.doodle.dto.response.UserResponse;
import com.doodle.entity.User;
import com.doodle.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse create(CreateUserRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        return map(userRepository.save(user));
    }

    public UserResponse getById(Long id) {

        return map(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public List<UserResponse> getAll() {

        return userRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private UserResponse map(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
