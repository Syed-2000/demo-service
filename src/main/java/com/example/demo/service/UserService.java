package com.example.demo.service;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UserService {

     Mono<UserResponseDTO> createUser(UserRequestDTO requestDTO);
     Flux<UserResponseDTO> getUsersByAgeAndStatus(int minAge, int maxAge, String status);
     Mono<UserResponseDTO> getUserById(Long id);
}