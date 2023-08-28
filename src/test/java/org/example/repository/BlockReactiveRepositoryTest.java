package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.Block;
import org.example.repository.friendshiprepository.BlockReactiveRepository;
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
public class BlockReactiveRepositoryTest extends TestCase {

    @Mock
    private BlockReactiveRepository blockReactiveRepository;

    @Test
    public void testFindByBlockerIdAndBlockedId() {
        // Prepare for data

        int blockerId = 1;
        int blockedId = 2;

        Block expectedBlock = new Block();
        expectedBlock.setBlockerId(blockerId);
        expectedBlock.setBlockedId(blockedId);

        // Mock

        when(blockReactiveRepository.findByBlockerIdAndBlockedId(eq(blockerId), eq(blockedId)))
                .thenReturn(Mono.just(expectedBlock));

        // Invoke method

        Mono<Block> actualBlock = blockReactiveRepository.findByBlockerIdAndBlockedId(blockerId, blockedId);

        // Verify the result

        StepVerifier.create(actualBlock)
                .expectNext(expectedBlock)
                .verifyComplete();
        verify(blockReactiveRepository, times(1)).findByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Test
    public void findByBlockerId() {
        // Prepare for data

        int blockerId = 1;

        Block expectedBlock = new Block();
        expectedBlock.setBlockerId(1);
        expectedBlock.setBlockedId(2);

        // Mock

        when(blockReactiveRepository.findByBlockerId(1))
                .thenReturn(Mono.just(expectedBlock));

        // Invoke method

        Mono<Block> actualBlock = blockReactiveRepository.findByBlockerId(blockerId);

        // Verify the result

        verify(blockReactiveRepository, times(1)).findByBlockerId(blockerId);

        StepVerifier.create(actualBlock)
                .expectNext(expectedBlock)
                .verifyComplete();
    }
}