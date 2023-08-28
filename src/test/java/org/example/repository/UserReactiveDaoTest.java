package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.User;
import org.example.repository.friendshiprepository.UserReactiveDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@DataR2dbcTest
@RunWith(SpringRunner.class)
public class UserReactiveDaoTest extends TestCase {
    @Mock
    private UserReactiveDao userReactiveDao;

    @Test
    public void testFindByEmail() {
        // Prepare for data

        String email = "andy@example.com";

        User expectUser = new User();
        expectUser.setUserId(1);
        expectUser.setEmail(email);

        // Mock

        when(userReactiveDao.findByEmail(email))
                .thenReturn(Mono.just(expectUser));

        // Invoke method

        Mono<User> actualUser = userReactiveDao.findByEmail(email);

        // Verify the result

        verify(userReactiveDao, times(1)).findByEmail(email);

        StepVerifier.create(actualUser)
                .expectNext(expectUser)
                .verifyComplete();
    }

    @Test
    public void testFindByUserId() {
        // Prepare for data

        int userId = 1;

        User expectUser = new User();
        expectUser.setUserId(userId);

        // Mock
        when(userReactiveDao.findByUserId(anyInt()))
                .thenReturn(Mono.just(expectUser));

        // Invoke method
        Mono<User> actualUser = userReactiveDao.findByUserId(userId);

        // Verify the result

        verify(userReactiveDao, times(1)).findByUserId(userId);

        StepVerifier.create(actualUser)
                .expectNext(expectUser)
                .verifyComplete();
    }
}