package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.Friendship;
import org.example.repository.friendshiprepository.FriendshipReactiveDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

        Friendship expectedFriendship1 = new Friendship();
        expectedFriendship1.setFriendshipId(1);
        expectedFriendship1.setStatus("accepted");
        expectedFriendship1.setUserId(1);
        expectedFriendship1.setFriendshipId(2);

        Friendship expectedFriendship2 = new Friendship();
        expectedFriendship2.setFriendshipId(1);
        expectedFriendship2.setStatus("accepted");
        expectedFriendship2.setUserId(1);
        expectedFriendship2.setFriendshipId(3);

        // Mock

        when(friendshipReactiveDao.findByUserIdAndStatus(anyInt(), anyString()))
                .thenReturn(Flux.just(expectedFriendship1, expectedFriendship2));

        // Invoke method

        Flux<Friendship> actualFriendship = friendshipReactiveDao.findByUserIdAndStatus(userId, status);

        // Verify the result

        StepVerifier.create(actualFriendship)
                .expectNext(expectedFriendship1)
                .expectNext(expectedFriendship2)
                .verifyComplete();
        verify(friendshipReactiveDao, times(1)).findByUserIdAndStatus(userId, status);
    }

    @Test
    public void testFindByUserIdAndFriendId() {
        // Prepare for data

        int userId = 1;
        int friendId = 2;

        Friendship expectFriendship = new Friendship();
        expectFriendship.setUserId(userId);
        expectFriendship.setFriendId(friendId);

        // Mock

        when(friendshipReactiveDao.findByUserIdAndFriendId(eq(userId), eq(friendId)))
                .thenReturn(Mono.just(expectFriendship));

        // Invoke method

        Mono<Friendship> actualFriendship = friendshipReactiveDao.findByUserIdAndFriendId(userId, friendId);

        // Verify the result

        StepVerifier.create(actualFriendship)
                .expectNext(expectFriendship)
                .verifyComplete();
        verify(friendshipReactiveDao, times(1)).findByUserIdAndFriendId(userId, friendId);

    }
}