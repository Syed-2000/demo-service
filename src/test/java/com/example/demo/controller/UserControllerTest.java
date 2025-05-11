package com.example.demo.controller;

import com.example.demo.dto.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToController(userController).build();
    }

    @Test
    public void testCreateUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("John Doe", 30, "ACTIVE");

        UserResponseDTO userResponseDTO = new UserResponseDTO(1L, "John Doe", 30, "ACTIVE");

        
        Mockito.when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(Mono.just(userResponseDTO));

        
        webTestClient.post()
                .uri("/api/users")
                .bodyValue(userRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.age").isEqualTo(30)
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    public void testGetUserById() {
        UserResponseDTO userResponseDTO = new UserResponseDTO(1L, "John Doe", 30, "ACTIVE");

        
        Mockito.when(userService.getUserById(1L))
                .thenReturn(Mono.just(userResponseDTO));

        
        webTestClient.get()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.name").isEqualTo("John Doe")
                .jsonPath("$.age").isEqualTo(30)
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    public void testGetUserById_NotFound() {
        
        Mockito.when(userService.getUserById(99L))
                .thenReturn(Mono.empty());

        
        webTestClient.get()
                .uri("/api/users/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testSearchUsers() {
        UserResponseDTO userResponseDTO = new UserResponseDTO(1L, "John Doe", 30, "ACTIVE");

        
        Mockito.when(userService.getUsersByAgeAndStatus(20, 40, "ACTIVE"))
                .thenReturn(Flux.just(userResponseDTO));

        
        webTestClient.get()
                .uri("/api/users/search?minAge=20&maxAge=40&status=ACTIVE")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("John Doe")
                .jsonPath("$[0].age").isEqualTo(30)
                .jsonPath("$[0].status").isEqualTo("ACTIVE");
    }
}
