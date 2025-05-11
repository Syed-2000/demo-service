package com.example.demo.repository;


import com.example.demo.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Flux<User> findByAgeBetweenAndStatus(int minAge, int maxAge, String status);
}