package org.example.repository;


import org.example.model.friends.Subscription;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface SubscriptionReactiveDao extends R2dbcRepository<Subscription, Integer> {
    Mono<Subscription> findBySubscriberIdAndTargetId(int subscriberId, int targetId);
    Mono<Subscription> deleteBySubscriberIdAndTargetId(int subscriberId, int targetId);
}
