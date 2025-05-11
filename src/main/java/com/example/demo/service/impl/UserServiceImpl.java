package com.example.demo.service.impl;

import com.example.demo.utils.ErrorMessages;
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
            return Mono.error(new IllegalArgumentException(ErrorMessages.USER_REQUEST_NULL));
        }

        if (!StringUtils.hasText(requestDTO.status())) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.USER_STATUS_BLANK));
        }

        if (requestDTO.age() < 0) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.USER_AGE_NEGATIVE));
        }

        User user = userMapper.toEntity(requestDTO);
        if (user == null) {
            return Mono.error(new IllegalStateException(ErrorMessages.USER_ENTITY_MAPPING_FAILED));
        }

        return userRepository.save(user)
                .map(savedUser -> {
                    UserResponseDTO dto = userMapper.toResponseDto(savedUser);
                    if (dto == null) {
                        throw new IllegalStateException(ErrorMessages.USER_RESPONSE_MAPPING_FAILED);
                    }
                    return dto;
                })
                .switchIfEmpty(Mono.error(new IllegalStateException(ErrorMessages.USER_CREATION_FAILED)));
    }

    @Override
    public Mono<UserResponseDTO> getUserById(Long id) {
        if (id == null || id <= 0) {
            return Mono.error(new IllegalArgumentException(ErrorMessages.USER_ID_INVALID));
        }

        return userRepository.findById(id)
                .map(user -> {
                    UserResponseDTO dto = userMapper.toResponseDto(user);
                    if (dto == null) {
                        throw new IllegalStateException(ErrorMessages.USER_RESPONSE_MAPPING_FAILED);
                    }
                    return dto;
                })
                .switchIfEmpty(Mono.error(new RuntimeException(String.format(ErrorMessages.USER_NOT_FOUND, id))));
    }

    @Override
    public Flux<UserResponseDTO> getUsersByAgeAndStatus(int minAge, int maxAge, String status) {
        if (minAge < 0 || maxAge < 0) {
            return Flux.error(new IllegalArgumentException(ErrorMessages.AGE_NEGATIVE));
        }

        if (minAge > maxAge) {
            return Flux.error(new IllegalArgumentException(ErrorMessages.AGE_RANGE_INVALID));
        }

        if (!StringUtils.hasText(status)) {
            return Flux.error(new IllegalArgumentException(ErrorMessages.STATUS_BLANK));
        }

        return userRepository.findByAgeBetweenAndStatus(minAge, maxAge, status)
                .map(user -> {
                    UserResponseDTO dto = userMapper.toResponseDto(user);
                    if (dto == null) {
                        throw new IllegalStateException(ErrorMessages.USER_RESPONSE_MAPPING_FAILED);
                    }
                    return dto;
                });
    }
}
