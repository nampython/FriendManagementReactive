package org.example.repository;

import junit.framework.TestCase;
import org.example.model.friends.Block;
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
public class BlockReactiveRepositoryTest extends TestCase {

    @Mock
    private BlockReactiveRepository blockReactiveRepository;

    @Test
    public void testFindByBlockerIdAndBlockedId() {
        // Prepare for data
        int blockerId = 1;
        int blockedId = 2;

        Block block = new Block();
        block.setBlockerId(blockerId);
        block.setBlockedId(blockedId);

        // Mock
        when(blockReactiveRepository.findByBlockerIdAndBlockedId(eq(blockerId), eq(blockedId)))
                .thenReturn(Mono.just(block));

        // Invoke method
        Mono<Block> result = blockReactiveRepository.findByBlockerIdAndBlockedId(blockerId, blockedId);

        // Verify the result
        StepVerifier.create(result)
                .expectNext(block)
                .verifyComplete();
    }
}