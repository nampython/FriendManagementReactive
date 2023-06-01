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
import static org.mockito.Mockito.*;

@DataR2dbcTest
@RunWith(SpringRunner.class)
public class SubscriptionReactiveDaoTest extends TestCase {
    @Mock
    private SubscriptionReactiveDao subscriptionReactiveDao;

    @Test
    public void testFindBySubscriberIdAndTargetId() {
        // Prepare for data
        int subscriberId = 1;
        int targetId = 2;

        Subscription expectedsubscription = new Subscription();
        expectedsubscription.setSubscriberId(subscriberId);
        expectedsubscription.setTargetId(targetId);

        // Mock
        when(subscriptionReactiveDao.findBySubscriberIdAndTargetId(eq(subscriberId), eq(targetId)))
                .thenReturn(Mono.just(expectedsubscription));

        // Invoke method
        Mono<Subscription> actualSubscription = subscriptionReactiveDao.findBySubscriberIdAndTargetId(subscriberId, targetId);

        // Verify the result
        StepVerifier.create(actualSubscription)
                .expectNext(expectedsubscription)
                .verifyComplete();

        verify(subscriptionReactiveDao, times(1)).findBySubscriberIdAndTargetId(subscriberId, targetId);
    }

    @Test
    public void testDeleteBySubscriberIdAndTargetId() {
        // Prepare for data
        int subscriberId = 1;
        int targetId = 2;

        Subscription expectedsubscription = new Subscription();
        expectedsubscription.setSubscriberId(subscriberId);
        expectedsubscription.setTargetId(targetId);

        // Mock
        when(subscriptionReactiveDao.deleteBySubscriberIdAndTargetId(eq(subscriberId), eq(targetId)))
                .thenReturn(Mono.just(expectedsubscription));

        // Invoke method
        Mono<Subscription> actualSubscription = subscriptionReactiveDao.deleteBySubscriberIdAndTargetId(subscriberId, targetId);

        // Verify the result
        StepVerifier.create(actualSubscription)
                .expectNext(expectedsubscription)
                .verifyComplete();

        verify(subscriptionReactiveDao, times(1)).deleteBySubscriberIdAndTargetId(subscriberId, targetId);
    }
}