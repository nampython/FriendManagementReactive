package org.example.controller;

import org.example.model.CommonFriend;
import org.example.model.FriendList;
import org.example.model.ResponseObject;
import org.example.model.UpdateEmail;
import org.example.model.friends.Friendship;
import org.example.model.friends.Subscription;
import org.example.service.FriendShipReactiveService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(CustomerReactiveController.class)
@RunWith(SpringRunner.class)
public class CustomerReactiveControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private FriendShipReactiveService friendShipReactiveService;
    @Mock
    private CustomerReactiveController customerReactiveController;


    @BeforeEach
    public void setup() {
        webClient = WebTestClient.bindToController(customerReactiveController).build();
    }

    @Test
    public void getFriendList() {
        // Prepare for Data

        String friend1 = "andy@example.com";
        String friend2 = "john@example.com";
        List<String> friends = Arrays.asList(friend1, friend2);


        FriendList friendList = new FriendList();
        friendList.setFriends(friends);
        friendList.setCount(friends.size());

        ResponseObject responseObject = new ResponseObject();
        responseObject.setMessage("Friend list retrieved successfully.");
        responseObject.setSuccess("true");
        responseObject.setResult(friendList);


        // Mock
        when(friendShipReactiveService.getFriendsListByEmail(anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().body(responseObject)));

        // Verify the response

        webClient
                .get()
                .uri("/v1/user/friends?email=test@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("Friend list retrieved successfully.")
                .jsonPath("$.result.count").isEqualTo(2)
                .jsonPath("$.result.friends[0]").isEqualTo("andy@example.com")
                .jsonPath("$.result.friends[1]").isEqualTo("john@example.com");
    }

    @Test
    public void getCommonFriends() {
        // Prepare for Data

        String friend1 = "andy@example.com";
        String friend2 = "john@example.com";
        List<String> commonFriends = Arrays.asList(friend1, friend2);

        CommonFriend commonFriend = new CommonFriend();
        commonFriend.setFriends(commonFriends);
        commonFriend.setCount(commonFriends.size());

        ResponseObject responseObject = new ResponseObject();
        responseObject.setMessage("Common Friend list retrieved successfully.");
        responseObject.setSuccess("true");
        responseObject.setResult(commonFriend);

        // Mock

        when(friendShipReactiveService.getCommonFriends(anyString(), anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().body(responseObject)));

        // Verify the response

        webClient
                .get()
                .uri("/v1/user/common?email1=test1@example.com&email2=test2@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("Common Friend list retrieved successfully.")
                .jsonPath("$.result.count").isEqualTo(2)
                .jsonPath("$.result.friends[0]").isEqualTo("andy@example.com")
                .jsonPath("$.result.friends[1]").isEqualTo("john@example.com");

    }

    @Test
    public void createConnectionFriend() {
        // Prepare for data

        Friendship friendship = new Friendship();
        friendship.setUserId(1);
        friendship.setFriendId(2);

        ResponseObject responseObject = new ResponseObject();
        responseObject.setMessage("The connection is established successfully.");
        responseObject.setSuccess("true");
        responseObject.setResult(friendship);

        // Mock

        when(friendShipReactiveService.createFriendConnection(anyString(), anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().body(responseObject)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/connect?email1=andy@example.com&email2=kate@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("The connection is established successfully.")
                .jsonPath("$.result.userId").isEqualTo(1)
                .jsonPath("$.result.friendId").isEqualTo(2);
    }

    @Test
    public void subscribeToUpdates() {
        // Prepare for data

        int subscriberEmailId = 1;
        int targetEmailId = 2;
        Subscription subscription = new Subscription();
        subscription.setSubscriberId(subscriberEmailId);
        subscription.setTargetId(targetEmailId);

        ResponseObject responseObject = new ResponseObject();
        responseObject.setMessage("Subscribed successfully.");
        responseObject.setResult(subscription);
        responseObject.setSuccess("true");

        // Mock
        when(friendShipReactiveService.subscribeToUpdates(anyString(), anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().body(responseObject)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/subscribe?email1=john@example.com&email2=andy@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("Subscribed successfully.")
                .jsonPath("$.result.subscriberId").isEqualTo(1)
                .jsonPath("$.result.targetId").isEqualTo(2);
    }

    @Test
    public void block() {
    }

    @Test
    public void getEligibleEmailAddresses() {
        // Prepare for data

        String friend1 = "andy@example.com";
        String friend2 = "john@example.com";
        List<String> friends = Arrays.asList(friend1, friend2);

        UpdateEmail updateEmail = new UpdateEmail();
        updateEmail.setFriends(friends);
        updateEmail.setCount(friends.size());

        ResponseObject responseObject = new ResponseObject();
        responseObject.setMessage("Friend list retrieved successfully.");
        responseObject.setSuccess("true");
        responseObject.setResult(updateEmail);

        // Mock
        when(friendShipReactiveService.getEligibleEmailAddresses(anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().body(responseObject)));

        // Verify the response

        webClient
                .get()
                .uri("/v1/user/updatable?email=andy@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("Friend list retrieved successfully.")
                .jsonPath("$.result.count").isEqualTo(2)
                .jsonPath("$.result.friends[0]").isEqualTo("andy@example.com")
                .jsonPath("$.result.friends[1]").isEqualTo("john@example.com");
    }
}