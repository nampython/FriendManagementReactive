package org.example.repository;


import org.example.model.friends.Subscription;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface SubscriptionReactiveDao extends R2dbcRepository<Subscription, Integer> {
    Mono<Subscription> findBySubscriberIdAndTargetId(Integer subscriberId, Integer targetId);
    Mono<Subscription> deleteBySubscriberIdAndTargetId(Integer subscriberId, Integer targetId);
     Mono<Subscription> findBySubscriberId(Integer userId);
}
