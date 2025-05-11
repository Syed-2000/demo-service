package com.example.demo.repository;

import com.example.demo.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll().block();
    }

    @Test
    @DisplayName("1. Save user - should return saved user with ID")
    public void saveUser_shouldReturnSavedUser() {
        User user = new User.Builder().name("user1").age(10).status("active").build();

        Mono<User> savedUser = userRepository.save(user);

        StepVerifier.create(savedUser)
                .assertNext(u -> {
                    Assertions.assertNotNull(u.getId());
                    Assertions.assertEquals("user1", u.getName());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("2. Find all users - should return saved users")
    public void findAll_shouldReturnAllUsers() {
        User user1 = new User.Builder().name("user1").age(10).status("active").build();
        User user2 = new User.Builder().name("user2").age(20).status("active").build();

        userRepository.saveAll(Flux.just(user1, user2)).blockLast();

        StepVerifier.create(userRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("3. Find by ID - should return correct user")
    public void findById_shouldReturnUser() {
        User user = new User.Builder().name("user1").age(10).status("active").build();
        User savedUser = userRepository.save(user).block();

        Mono<User> foundUser = userRepository.findById(savedUser.getId());

        StepVerifier.create(foundUser)
                .expectNextMatches(u -> u.getName().equals("user1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("4. Find by age range and status - should return filtered users")
    public void findByAgeBetweenAndStatus_shouldReturnMatchingUsers() {
        User user1 = new User.Builder().name("user1").age(10).status("active").build();
        User user2 = new User.Builder().name("user2").age(20).status("active").build();
        User user3 = new User.Builder().name("user3").age(30).status("inactive").build();
        User user4 = new User.Builder().name("user4").age(40).status("active").build();
        User user5 = new User.Builder().name("user5").age(50).status("inactive").build();

        userRepository.saveAll(Flux.just(user1, user2, user3, user4, user5)).blockLast();

        StepVerifier.create(userRepository.findByAgeBetweenAndStatus(10, 40, "active"))
                .expectNextCount(3)
                .verifyComplete();

        StepVerifier.create(userRepository.findByAgeBetweenAndStatus(10, 50, "inactive"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("5. Update user - should return updated user")
    public void updateUser_shouldReturnUpdatedUser() {
        User user = new User.Builder().name("user1").age(10).status("active").build();
        User savedUser = userRepository.save(user).block();

        savedUser.setName("user1Updated");

        Mono<User> updatedUser = userRepository.save(savedUser);

        StepVerifier.create(updatedUser)
                .expectNextMatches(u -> u.getName().equals("user1Updated"))
                .verifyComplete();
    }

    @Test
    @DisplayName("6. Delete user - should result in empty findById")
    public void deleteUser_shouldResultInEmptyMono() {
        User user = new User.Builder().name("user1").age(10).status("active").build();
        User savedUser = userRepository.save(user).block();

        userRepository.deleteById(savedUser.getId()).block();

        Mono<User> deletedUser = userRepository.findById(savedUser.getId());

        StepVerifier.create(deletedUser)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("7. Find non-existent user - should return empty")
    public void findNonExistentUser_shouldReturnEmpty() {
        Mono<User> user = userRepository.findById(999L);

        StepVerifier.create(user)
                .expectComplete()
                .verify();
    }
}
