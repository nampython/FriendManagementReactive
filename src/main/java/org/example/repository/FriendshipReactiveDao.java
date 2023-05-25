package org.example.repository;

import org.example.model.friends.Friendship;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FriendshipReactiveDao extends R2dbcRepository<Friendship, Integer> {
    Flux<Friendship> findByUserIdAndStatus(int userId, String status);
    Mono<Friendship> findByUserIdAndFriendId(int userId, int friendId);
}
