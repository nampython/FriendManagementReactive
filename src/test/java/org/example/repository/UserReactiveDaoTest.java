package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@DataR2dbcTest
@RunWith(SpringRunner.class)
public class UserReactiveDaoTest extends TestCase {
    @Mock
    private UserReactiveDao userReactiveDao;

    @Test
    public void testFindByEmail() {
        // Prepare for data
        String email = "andy@example.com";

        User user = new User();
        user.setUserId(1);
        user.setEmail(email);

        // Mock
        when(userReactiveDao.findByEmail(anyString())).thenReturn(Mono.just(user));

        // Invoke method
        Mono<User> result = userReactiveDao.findByEmail(email);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();

    }

    @Test
    public void testFindByUserId() {
        // Prepare for data
        int userId = 1;

        User user = new User();
        user.setUserId(userId);

        // Mock
        when(userReactiveDao.findByUserId(anyInt())).thenReturn(Mono.just(user));

        // Invoke method
        Mono<User> result = userReactiveDao.findByUserId(userId);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}