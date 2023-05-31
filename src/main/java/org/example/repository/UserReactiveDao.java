package org.example.repository;

import org.example.model.friends.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveDao extends R2dbcRepository<User, Integer> {
    Mono<User> findByEmail(String email);
    Mono<User> findByUserId(Integer userId);
}
