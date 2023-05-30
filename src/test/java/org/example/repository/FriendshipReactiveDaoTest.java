package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.Friendship;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DataR2dbcTest
@RunWith(SpringRunner.class)
public class FriendshipReactiveDaoTest extends TestCase {
    @Mock
    private FriendshipReactiveDao friendshipReactiveDao;

    @Test
    public void testFindByUserIdAndStatus() {
        // Prepare for data
        int userId = 1;
        String status = "accepted";

        Friendship friendship1 = new Friendship();
        friendship1.setFriendshipId(1);
        friendship1.setStatus("accepted");
        friendship1.setUserId(1);
        friendship1.setFriendshipId(2);

        Friendship friendship2 = new Friendship();
        friendship2.setFriendshipId(1);
        friendship2.setStatus("accepted");
        friendship2.setUserId(1);
        friendship2.setFriendshipId(3);

        // Mock
        when(friendshipReactiveDao.findByUserIdAndStatus(anyInt(), anyString()))
                .thenReturn(Flux.just(friendship1, friendship2));

        // Invoke method
        Flux<Friendship> result = friendshipReactiveDao.findByUserIdAndStatus(userId, status);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(friendship1)
                .expectNext(friendship2)
                .verifyComplete();
    }

    @Test
    public void testFindByUserIdAndFriendId() {
        // Prepare for data
        int userId = 1;
        int friendId = 2;

        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);

        // Mock
        when(friendshipReactiveDao.findByUserIdAndFriendId(eq(userId), eq(friendId)))
                .thenReturn(Mono.just(friendship));

        // Invoke method
        Mono<Friendship> result = friendshipReactiveDao.findByUserIdAndFriendId(userId, friendId);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(friendship)
                .verifyComplete();
    }
}