package org.example.repository;

import org.example.model.friends.Block;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface BlockReactiveRepository extends R2dbcRepository<Block, Integer> {
    Mono<Block> findByBlockerIdAndBlockedId(int blockerId, int blockedId);
}
