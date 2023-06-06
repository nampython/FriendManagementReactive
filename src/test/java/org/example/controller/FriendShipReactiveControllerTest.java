package org.example.controller;

import org.example.dto.*;
import org.example.model.Response;
import org.example.model.friends.Friendship;
import org.example.model.friends.Subscription;
import org.example.service.FriendShipReactiveService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(FriendShipReactiveController.class)
@RunWith(SpringRunner.class)
public class FriendShipReactiveControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private FriendShipReactiveService friendShipReactiveService;

    @Test
    public void getFriendList() {
        // Prepare for Data

        String friend1 = "andy@example.com";
        String friend2 = "john@example.com";
        List<String> friends = Arrays.asList(friend1, friend2);

        FriendListDTO.Response expecResponse = FriendListDTO.Response.builder()
                .friends(friends)
                .count(friends.size())
                .build();

        Response expectResponse = new Response();
        expectResponse.setMessage("Friend list retrieved successfully.");
        expectResponse.setSuccess("true");
        expectResponse.setResult(expecResponse);


        // Mock

        FriendListDTO.Request request = FriendListDTO.Request.builder()
                .email("kate@example.com")
                .build();

        when(friendShipReactiveService.getFriendsListByEmail(request))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body(expectResponse)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/friends")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
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

        String friend1 = "friend1@example.com";
        String friend2 = "friend2@example.com";
        List<String> expectedFriends = Arrays.asList(friend1, friend2);

        CommonFriendDTO.Response expectedResponse = CommonFriendDTO.Response.builder()
                .friends(expectedFriends)
                .count(expectedFriends.size())
                .build();


        Response response = new Response();
        response.setMessage("Common Friend list retrieved successfully.");
        response.setSuccess("true");
        response.setResult(expectedResponse);

        // Mock

        CommonFriendDTO.Request request = CommonFriendDTO.Request.builder()
                .email1("john@example.com")
                .email2("andy@example.com")
                .build();
        when(friendShipReactiveService.getCommonFriends(request))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body(response)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/common")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("Common Friend list retrieved successfully.")
                .jsonPath("$.result.count").isEqualTo(2)
                .jsonPath("$.result.friends[0]").isEqualTo("friend1@example.com")
                .jsonPath("$.result.friends[1]").isEqualTo("friend2@example.com");

    }

    @Test
    public void createConnectionFriend() {
        // Prepare for data

        Friendship expectedFriendship = Friendship.builder()
                .userId(1)
                .friendId(2)
                .build();

        Response response = new Response();
        response.setMessage("The connection is established successfully.");
        response.setSuccess("true");
        response.setResult(expectedFriendship);

        // Mock

        FriendConnectionDTO.Request request = FriendConnectionDTO.Request.builder()
                .email1("andy@example.com")
                .email2("john@example.com")
                .build();
        when(friendShipReactiveService.createFriendConnection(request))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body(response)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/connect?email1=andy@example.com&email2=kate@example.com")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
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
        Subscription expectedsubscription = new Subscription();
        expectedsubscription.setSubscriberId(subscriberEmailId);
        expectedsubscription.setTargetId(targetEmailId);

        SubscribeUpdatesDTO.Response expectResponse = SubscribeUpdatesDTO.Response.builder()
                .subscription(expectedsubscription)
                .build();

        Response response = new Response();
        response.setMessage("Subscribed successfully.");
        response.setResult(expectResponse);
        response.setSuccess("true");

        // Mock

        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
                .email1("andy@example.com")
                .email2("john@example.com")
                .build();

        when(friendShipReactiveService.subscribeToUpdates(request))
                .thenReturn(Mono.just(ResponseEntity.ok().body(response)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/subscribe")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo("true")
                .jsonPath("$.message").isEqualTo("Subscribed successfully.")
                .jsonPath("$.result.subscription.subscriberId").isEqualTo(1)
                .jsonPath("$.result.subscription.targetId").isEqualTo(2);
    }

    @Test
    public void getEligibleEmailAddresses() {
        // Prepare for data

        String friend1 = "andy@example.com";
        String friend2 = "john@example.com";
        List<String> expectedFriends = Arrays.asList(friend1, friend2);

        EligibleEmailAddressesDTO.Response expectedResponse = EligibleEmailAddressesDTO.Response
                .builder()
                .friends(expectedFriends)
                .count(expectedFriends.size())
                .build();

        Response expectedResponseObject = new Response();
        expectedResponseObject.setMessage("Friend list retrieved successfully.");
        expectedResponseObject.setSuccess("true");
        expectedResponseObject.setResult(expectedResponse);

        // Mock

        EligibleEmailAddressesDTO.Request request = EligibleEmailAddressesDTO.Request.builder()
                .email("test1@gmail.com")
                .build();
        when(friendShipReactiveService.getEligibleEmailAddresses(request))
                .thenReturn(Mono.just(ResponseEntity.ok().body(expectedResponseObject)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/updatable")
                .body(BodyInserters.fromValue(request))
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
    public void block() {
        // Prepare for data

        String friend1 = "andy@example.com";
        String friend2 = "john@example.com";
        List<String> expectedFriends = Arrays.asList(friend1, friend2);

        EligibleEmailAddressesDTO.Response expectedResponse = EligibleEmailAddressesDTO.Response
                .builder()
                .friends(expectedFriends)
                .count(expectedFriends.size())
                .build();

        Response expectedResponseObject = new Response();
        expectedResponseObject.setMessage("Friend list retrieved successfully.");
        expectedResponseObject.setSuccess("true");
        expectedResponseObject.setResult(expectedResponse);

        // Mock

        EligibleEmailAddressesDTO.Request request = EligibleEmailAddressesDTO.Request.builder()
                .email("test1@gmail.com")
                .build();
        when(friendShipReactiveService.getEligibleEmailAddresses(request))
                .thenReturn(Mono.just(ResponseEntity.ok().body(expectedResponseObject)));

        // Verify the response

        webClient
                .post()
                .uri("/v1/user/updatable")
                .body(BodyInserters.fromValue(request))
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
