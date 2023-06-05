package org.example.service;

import junit.framework.TestCase;
import org.example.dto.*;
import org.example.model.Response;
import org.example.model.friends.Friendship;
import org.example.model.friends.Subscription;
import org.example.model.friends.User;
import org.example.repository.BlockReactiveRepository;
import org.example.repository.FriendshipReactiveDao;
import org.example.repository.SubscriptionReactiveDao;
import org.example.repository.UserReactiveDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DataR2dbcTest
@RunWith(SpringRunner.class)
public class FriendShipReactiveServiceImplTest extends TestCase {
    @Mock
    private UserReactiveDao userReactiveDao;
    @Mock
    private FriendshipReactiveDao friendshipReactiveDao;
    @Mock
    private SubscriptionReactiveDao subscriptionReactiveDao;
    @Mock
    private BlockReactiveRepository blockReactiveRepository;
    @InjectMocks
    private FriendShipReactiveServiceImpl friendShipReactiveService;

    @Test
    public void testGetFriendsListBy_ValidEmail() {
        // Prepare for data

        String email = "test@example.com";
        User user = new User();

        user.setUserId(1);
        user.setEmail(email);

        Friendship friendship1 = new Friendship();
        friendship1.setFriendshipId(1);
        friendship1.setUserId(1);
        friendship1.setFriendId(2);
        friendship1.setStatus("accepted");

        Friendship friendship2 = new Friendship();
        friendship2.setFriendshipId(2);
        friendship2.setUserId(1);
        friendship2.setFriendId(3);
        friendship2.setStatus("accepted");

        User friend1 = new User();
        friend1.setUserId(2);
        friend1.setEmail("friend1@example.com");

        User friend2 = new User();
        friend2.setUserId(3);
        friend2.setEmail("friend2@example.com");

        // Mock

        when(userReactiveDao.findByEmail(eq(email))).thenReturn(Mono.just(user));
        when(friendshipReactiveDao.findByUserIdAndStatus(eq(1), eq("accepted")))
                .thenReturn(Flux.just(friendship1, friendship2));
        when(userReactiveDao.findByUserId(eq(2))).thenReturn(Mono.just(friend1));
        when(userReactiveDao.findByUserId(eq(3))).thenReturn(Mono.just(friend2));

        // Invoke method

        FriendListDTO.Request request = new FriendListDTO.Request("test@example.com");
        Mono<ResponseEntity<Response>> result = friendShipReactiveService.getFriendsListByEmail(request);

        // Verify the result

        List<String> expectedFriends = Arrays.asList("friend1@example.com", "friend2@example.com");

        FriendListDTO.Response expectedResponseDTO = FriendListDTO.Response.builder()
                .friends(expectedFriends)
                .count(expectedFriends.size())
                .build();

        Response expectedResponse = Response.builder()
                .message("Friend list retrieved successfully.")
                .success("true")
                .result(expectedResponseDTO)
                .build();
        ResponseEntity<Response> expectedResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(result)
                .expectNext(expectedResponseEntity)
                .verifyComplete();

        // Verify the interactions with the mocks

        verify(userReactiveDao).findByEmail(email);

        verify(friendshipReactiveDao).findByUserIdAndStatus(user.getUserId(), "accepted");

        verify(userReactiveDao).findByUserId(friendship2.getFriendId());
    }

    @Test
    public void testGetFriendsListByEmail_InValidEmail() {
        // Prepare for data

        String invalidEmail = "usernamedomain.com";

        // Invoke method

        FriendListDTO.Request request = FriendListDTO.Request.builder()
                .email(invalidEmail)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.getFriendsListByEmail(request);

        // Verify the result

        Response expectedResponse = Response.builder()
                .message("Invalid email format {usernamedomain.com}. Please provide a valid email.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();

        // Verify that the methods were not called

        verifyNoInteractions(userReactiveDao, friendshipReactiveDao);
        verify(userReactiveDao, never()).findByEmail(invalidEmail);
    }

    @Test
    public void testGetCommonFriends() {
        // Prepare data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User user1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User user2 = User.builder()
                .userId(2)
                .email(email2)
                .build();


        Friendship friendship1 = new Friendship();
        friendship1.setUserId(1);
        friendship1.setFriendId(3);
        friendship1.setStatus("ACCEPTED");

        Friendship friendship2 = new Friendship();
        friendship2.setUserId(2);
        friendship2.setFriendId(3);
        friendship2.setStatus("ACCEPTED");

        User user3 = new User();
        user3.setUserId(3);
        user3.setEmail("user3@example.com");

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(user1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(user2));

        when(friendshipReactiveDao.findByUserIdAndStatus(1, "accepted"))
                .thenReturn(Flux.just(friendship1));

        when(friendshipReactiveDao.findByUserIdAndStatus(2, "accepted"))
                .thenReturn(Flux.just(friendship2));

        when(userReactiveDao.findByUserId(3))
                .thenReturn(Mono.just(user3));

        // Invoke Method

        CommonFriendDTO.Request actualRequest = CommonFriendDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualCommonFriends = friendShipReactiveService.getCommonFriends(actualRequest);

        // Verify the result

        String expectedEmail = "user3@example.com";

        List<String> expectListOfCommonFriends = List.of(expectedEmail);

        CommonFriendDTO.Response expectedResponse = CommonFriendDTO.Response.builder()
                .friends(expectListOfCommonFriends)
                .count(expectListOfCommonFriends.size())
                .build();

        Response expectResponse = Response.builder()
                .success("true")
                .message("Common Friend list retrieved successfully.")
                .result(expectedResponse)
                .build();

        ResponseEntity<Response> expectResEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualCommonFriends)
                .expectNext(expectResEntity)
                .verifyComplete();
    }

    @Test
    public void testGetCommonFriends_InvalidEmail() {
        // Prepare for data

        String invalidEmail1 = "usernamedomain.com";
        String invalidEmail2 = "username@.com";

        // Invoke method

        CommonFriendDTO.Request request = CommonFriendDTO.Request.builder()
                .email1(invalidEmail1)
                .email2(invalidEmail2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.getCommonFriends(request);

        // Verify the result

        Response expectedResponse = Response.builder()
                .message("Invalid email format {usernamedomain.com}. Please provide a valid email.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }


    @Test
    public void testCreateFriendConnection() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User user1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User user2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Friendship friendship = Friendship.builder()
                .friendshipId(1)
                .userId(1)
                .friendId(2)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(user1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(user2));

        when(friendshipReactiveDao.findByUserIdAndFriendId(1, 2))
                .thenReturn(Mono.empty());

        when(friendshipReactiveDao.save(any()))
                .thenReturn(Mono.just(friendship));

        // Invoke method

        FriendConnectionDTO.Request request = FriendConnectionDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();
        Mono<ResponseEntity<Response>> actualFriendConnection = friendShipReactiveService.createFriendConnection(request);

        // Verify the result

        Friendship expectedFriendship = Friendship.builder()
                .friendshipId(1)
                .userId(1)
                .friendId(2)
                .build();

        Response expectedResponse = Response.builder()
                .message("The connection is established successfully.")
                .success("true")
                .result(expectedFriendship)
                .build();

        ResponseEntity<Response> expectedResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualFriendConnection)
                .expectNext(expectedResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testCreateFriendConnection_InvalidEmail() {
        // Prepare for data

        String invalidEmail1 = "usernamedomain.com";
        String invalidEmail2 = "username@.com";

        // Invoke method

        FriendConnectionDTO.Request request = FriendConnectionDTO.Request.builder()
                .email1(invalidEmail1)
                .email2(invalidEmail2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.createFriendConnection(request);

        // Verify

        Response expectedResponse = Response.builder()
                .message("Invalid email format {usernamedomain.com}. Please provide a valid email.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testCreateFriendConnection_AlreadyFriend() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User user1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User user2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Friendship friendship = Friendship.builder()
                .friendshipId(1)
                .userId(1)
                .friendId(2)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(user1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(user2));

        when(friendshipReactiveDao.findByUserIdAndFriendId(1, 2))
                .thenReturn(Mono.just(friendship));

        // Invoke method

        FriendConnectionDTO.Request request = FriendConnectionDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.createFriendConnection(request);

        // Verify

        Response expectedResponse = Response.builder()
                .result(null)
                .success("true")
                .message("They are already friends. There is no need to create a new friend connection.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testCreateFriendConnection_NotFoundEmail1() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.empty());

        // Invoke method

        FriendConnectionDTO.Request request = FriendConnectionDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.createFriendConnection(request);

        // Verify the result

        Response expectedResponse = Response.builder()
                .result(null)
                .success("true")
                .message("One of the email address is not found.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testCreateFriendConnection_NotFoundEmail2() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User user1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        // Mock
        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(user1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.empty());

        // Invoke method

        FriendConnectionDTO.Request request = FriendConnectionDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.createFriendConnection(request);

        // Verify the result

        Response expectedResponse = Response.builder()
                .result(null)
                .success("true")
                .message("One of the email address is not found.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testSubscribeToUpdates() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User subscriberUser = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User targetUser = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Subscription newSub = Subscription.builder()
                .subscriberId(1)
                .targetId(2)
                .subscriptionId(1)
                .build();


        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(subscriberUser));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(targetUser));

        when(subscriptionReactiveDao.findBySubscriberIdAndTargetId(1, 2))
                .thenReturn(Mono.empty());

        when(subscriptionReactiveDao.save(any()))
                .thenReturn(Mono.just(newSub));

        // Invoke method

        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();
        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.subscribeToUpdates(request);


        // Verify the result

        Subscription expectNewSub = Subscription.builder()
                .subscriberId(1)
                .targetId(2)
                .subscriptionId(1)
                .build();

        SubscribeUpdatesDTO.Response expectNewSubDTO = SubscribeUpdatesDTO.Response
                .builder()
                .subscription(expectNewSub)
                .build();


        Response expectResponse = Response.builder()
                .message("Subscribed successfully.")
                .result(expectNewSubDTO)
                .success("true")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();

    }

    @Test
    public void testSubscribeToUpdates_InvalidEmail() {
        // Prepare for data

        String invalidEmail1 = "usernamedomain.com";
        String invalidEmail2 = "username@.com";

        // Invoke method

        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
                .email1(invalidEmail1)
                .email2(invalidEmail2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.subscribeToUpdates(request);

        // Verify

        Response expectedResponse = Response.builder()
                .message("Invalid email format {usernamedomain.com}. Please provide a valid email.")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

//    @Test
//    public void testSubscribeToUpdates_AlreadySubscription() {
//        // Prepare for data
//
//        String email1 = "andy@example.com";
//        String email2 = "john@example.com";
//
//        User subscriberUser = User.builder()
//                .userId(1)
//                .email(email1)
//                .build();
//        User targetUser = User.builder()
//                .userId(2)
//                .email(email2)
//                .build();
//
//        Subscription existSubscription = Subscription.builder()
//                .subscriptionId(1)
//                .subscriberId(1)
//                .targetId(2)
//                .build();
//
//        // Mock
//
//        when(userReactiveDao.findByEmail(email1))
//                .thenReturn(Mono.just(subscriberUser));
//
//        when(userReactiveDao.findByEmail(email2))
//                .thenReturn(Mono.just(targetUser));
//
//        when(subscriptionReactiveDao.findBySubscriberIdAndTargetId(anyInt(), anyInt()))
//                .thenReturn(Mono.just(existSubscription));
//
//        // Invoke method
//
//        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
//                .email1(email1)
//                .email2(email2)
//                .build();
//        Mono<ResponseEntity<ResponseObject>> actualResponseEntity = friendShipReactiveService.subscribeToUpdates(request);
//
//
//        // Verify the result
//
//        ResponseObject expectResponse = ResponseObject.builder()
//                .message("They already have a subscription.")
//                .result(existSubscription)
//                .success("true")
//                .build();
//
//        ResponseEntity<ResponseObject> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);
//
//        StepVerifier.create(actualResponseEntity)
//                .expectNext(expectResponseEntity)
//                .verifyComplete();
//    }


    @Test
    public void testSubscribeToUpdates_SubscriberUserNotFound() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User targetUser = User.builder()
                .userId(1)
                .email(email1)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.empty());

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(targetUser));

        // Invoke method

        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();
        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.subscribeToUpdates(request);


        // Verify the result

        Response expectResponse = Response.builder()
                .message("Subscriber user not found.")
                .success("true")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testSubscribeToUpdates_TargetUserNotFound() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User subscriberUser = User.builder()
                .userId(1)
                .email(email1)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(subscriberUser));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.empty());
        // Invoke method

        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();
        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.subscribeToUpdates(request);


        // Verify the result

        Response expectResponse = Response.builder()
                .message("Target user not found.")
                .success("true")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void getEligibleEmailAddresses() {
        // Prepare for data

        String email = "andy@example.com";
        User user = User.builder()
                .userId(1)
                .email(email)
                .build();

        String targetEmail1 = "kate@example.com";
        String targetEmail2 = "john@example.com";

        User targetUser1 = User.builder()
                .userId(3)
                .email(targetEmail1)
                .build();

        User targetUser2 = User.builder()
                .userId(4)
                .email(targetEmail2)
                .build();

        Friendship friendship = Friendship.builder()
                .friendshipId(1)
                .userId(1)
                .friendId(2)
                .build();

        Subscription subscription = Subscription.builder()
                .subscriptionId(1)
                .subscriberId(1)
                .targetId(2)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email))
                .thenReturn(Mono.just(user));

        when(friendshipReactiveDao.findByUserId(user.getUserId()))
                .thenReturn(Flux.just(friendship));

        when(subscriptionReactiveDao.findBySubscriberId(user.getUserId()))
                .thenReturn(Flux.just(subscription));

        when(blockReactiveRepository.findByBlockerId(friendship.getFriendId()))
                .thenReturn(Mono.empty());
        when(blockReactiveRepository.findByBlockerId(subscription.getTargetId()))
                .thenReturn(Mono.empty());

        when(userReactiveDao.findByUserId(friendship.getFriendId()))
                .thenReturn(Mono.just(targetUser1));

        when(userReactiveDao.findByUserId(subscription.getTargetId()))
                .thenReturn(Mono.just(targetUser2));


        // Invoke method

        EligibleEmailAddressesDTO.Request request = EligibleEmailAddressesDTO.Request
                .builder()
                .email(email)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.getEligibleEmailAddresses(request);


        // Verify the result

        Response expectedResponse = Response.builder()
                .message("Friend list retrieved successfully.")
                .success("true")
                .result(Arrays.asList(targetEmail1, targetEmail2))
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);


        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }


    public void testBlockUpdates() {
    }


}