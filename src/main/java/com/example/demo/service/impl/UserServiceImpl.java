package com.example.demo.service.impl;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Mono<UserResponseDTO> createUser(UserRequestDTO requestDTO) {
        if (requestDTO == null) {
            return Mono.error(new IllegalArgumentException("User request must not be null"));
        }
        if (!StringUtils.hasText(requestDTO.status())) {
            return Mono.error(new IllegalArgumentException("User status must not be blank"));
        }
        if (requestDTO.age() < 0) {
            return Mono.error(new IllegalArgumentException("User age must be non-negative"));
        }

        User user = userMapper.toEntity(requestDTO);
        if (user == null) {
            return Mono.error(new IllegalStateException("Mapping to User entity failed"));
        }

        return userRepository.save(user)
                .map(userMapper::toResponseDto)
                .switchIfEmpty(Mono.error(new IllegalStateException("User creation failed")));
    }

    @Override
    public Mono<UserResponseDTO> getUserById(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException("User ID must be positive"));
        }

        return userRepository.findById(id)
                .map(userMapper::toResponseDto)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + id)));
    }

    @Override
    public Flux<UserResponseDTO> getUsersByAgeAndStatus(int minAge, int maxAge, String status) {
        if (minAge < 0 || maxAge < 0) {
            return Flux.error(new IllegalArgumentException("Age must be non-negative"));
        }
        if (minAge > maxAge) {
            return Flux.error(new IllegalArgumentException("minAge must be less than or equal to maxAge"));
        }
        if (!StringUtils.hasText(status)) {
            return Flux.error(new IllegalArgumentException("Status must not be blank"));
        }

        return userRepository.findByAgeBetweenAndStatus(minAge, maxAge, status)
                .map(user -> {
                    UserResponseDTO dto = userMapper.toResponseDto(user);
                    if (dto == null) {
                        throw new IllegalStateException("User response mapping failed");
                    }
                    return dto;
                });
    }
}
