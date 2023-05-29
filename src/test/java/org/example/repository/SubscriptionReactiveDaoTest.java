package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.Subscription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@RunWith(SpringRunner.class)
public class SubscriptionReactiveDaoTest extends TestCase {
    @Mock
    private SubscriptionReactiveDao subscriptionReactiveDao;

    @Test
    public void testFindBySubscriberIdAndTargetId() {
        // Input
        int subscriberId = 1;
        int targetId = 2;

        // Sample data
        Subscription subscription = new Subscription();
        subscription.setSubscriberId(subscriberId);
        subscription.setTargetId(targetId);

        // Mock
        when(subscriptionReactiveDao.findBySubscriberIdAndTargetId(eq(subscriberId), eq(targetId)))
                .thenReturn(Mono.just(subscription));

        // Invoke method
        Mono<Subscription> result = subscriptionReactiveDao.findBySubscriberIdAndTargetId(subscriberId, targetId);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(subscription)
                .verifyComplete();
    }

    @Test
    public void testDeleteBySubscriberIdAndTargetId() {
        // Input
        int subscriberId = 1;
        int targetId = 2;

        // Sample data
        Subscription subscription = new Subscription();
        subscription.setSubscriberId(subscriberId);
        subscription.setTargetId(targetId);

        // Mock
        when(subscriptionReactiveDao.deleteBySubscriberIdAndTargetId(eq(subscriberId), eq(targetId)))
                .thenReturn(Mono.just(subscription));

        // Invoke method
        Mono<Subscription> result = subscriptionReactiveDao.deleteBySubscriberIdAndTargetId(subscriberId, targetId);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(subscription)
                .verifyComplete();
    }
}