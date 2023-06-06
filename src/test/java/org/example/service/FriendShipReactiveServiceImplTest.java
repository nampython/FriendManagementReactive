package org.example.service;

import junit.framework.TestCase;
import org.example.dto.*;
import org.example.model.Response;
import org.example.model.friends.Block;
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
import org.springframework.http.HttpMethod;
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

        String email = "andy@example.com";

        User expectedUser = User.builder()
                .userId(1)
                .email(email)
                .build();

        Friendship expectedFriendShip = Friendship.builder()
                .friendshipId(1)
                .userId(1)
                .friendId(2)
                .build();

        User expectedFriend = User.builder()
                .userId(2)
                .email("friend1@example.com")
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email))
                .thenReturn(Mono.just(expectedUser));

        when(friendshipReactiveDao.findByUserIdAndStatus(1, "accepted"))
                .thenReturn(Flux.just(expectedFriendShip));

        when(userReactiveDao.findByUserId(2))
                .thenReturn(Mono.just(expectedFriend));

        // Invoke method

        FriendListDTO.Request request = FriendListDTO.Request.builder()
                .email("andy@example.com")
                .build();
        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.getFriendsListByEmail(request);

        // Verify

        List<String> expectedFriends = Arrays.asList("friend1@example.com");

        FriendListDTO.Response expectedResponseDTO = FriendListDTO.Response.builder()
                .friends(expectedFriends)
                .count(expectedFriends.size())
                .build();

        Response expectedResponse = Response.builder()
                .method(HttpMethod.POST)
                .message("Friend list retrieved successfully.")
                .success("true")
                .result(expectedResponseDTO)
                .build();
        ResponseEntity<Response> expectedResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectedResponseEntity)
                .verifyComplete();
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
    public void testGetCommonFriends_NotFoundEmail1() {
        // Prepare data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";


        User user2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.empty());

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(user2));


        // Invoke Method

        CommonFriendDTO.Request request = CommonFriendDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualCommonFriends = friendShipReactiveService.getCommonFriends(request);

        // Verify the result


        Response expectResponse = Response.builder()
                .success("true")
                .message(String.format("Cannot find email {%s}. Please try another email", request.getEmail1()))
                .build();

        ResponseEntity<Response> expectResEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualCommonFriends)
                .expectNext(expectResEntity)
                .verifyComplete();
    }

    @Test
    public void testGetCommonFriends_NotFoundEmail2() {
        // Prepare data

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

        // Invoke Method

        CommonFriendDTO.Request request = CommonFriendDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualCommonFriends = friendShipReactiveService.getCommonFriends(request);

        // Verify the result


        Response expectResponse = Response.builder()
                .success("true")
                .message(String.format("Cannot find email {%s}. Please try another email", request.getEmail2()))
                .build();

        ResponseEntity<Response> expectResEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualCommonFriends)
                .expectNext(expectResEntity)
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
                .message(String.format("%s and %s are already friends. There is no need to create a new friend connection.", email1, email2))
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
                .message(String.format("Cannot find email {%s}. Please try another email", email2))
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
                .message(String.format("Cannot find email {%s}. Please try another email", email2))
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

    @Test
    public void testSubscribeToUpdates_AlreadySubscription() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User expectedSubscriberUser = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User expectedTargetUser = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Subscription expectedExistSub = Subscription.builder()
                .subscriptionId(1)
                .subscriberId(1)
                .targetId(2)
                .build();

        // Moc

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(expectedSubscriberUser));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(expectedTargetUser));

        when(subscriptionReactiveDao.findBySubscriberIdAndTargetId(anyInt(), anyInt()))
                .thenReturn(Mono.just(expectedExistSub));

        when(subscriptionReactiveDao.save(any()))
                .thenReturn(Mono.just(expectedExistSub));
        // Invoke method

        SubscribeUpdatesDTO.Request request = SubscribeUpdatesDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();
        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.subscribeToUpdates(request);


        // Verify the result

        Response expectResponse = Response.builder()
                .message("They already have a subscription.")
                .result(expectedExistSub)
                .success("true")
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }


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
                .message(String.format("Subscriber user {%s} not found, please try another email.", email1))
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
                .message(String.format("Target user {%s} not found, please try another email.", email2))
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
                .message("Retrieves the list successfully.")
                .success("true")
                .result(
                        EligibleEmailAddressesDTO.Response.builder()
                                .friends(Arrays.asList(targetEmail2))
                                .count(1)
                                .build()
                )
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);


        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void getEligibleEmailAddresses_InValidEmail() {
        // Prepare for data
        String email = "andyexample.com";

        // Mock

        // Invoke method

        EligibleEmailAddressesDTO.Request request = EligibleEmailAddressesDTO.Request.builder()
                .email(email)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.getEligibleEmailAddresses(request);

        // Verify
        Response expectedResponse = Response.builder()
                .message("Invalid email format {andyexample.com}. Please provide a valid email.")
                .method(HttpMethod.POST)
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testBlockUpdates() {
        // Prepare for data

        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User expectedUser1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User expectedUser2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Friendship expectedFriendShip = Friendship.builder()
                .friendshipId(1)
                .userId(1)
                .friendId(2)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(expectedUser1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(expectedUser2));

        when(friendshipReactiveDao.findByUserIdAndFriendId(1, 2))
                .thenReturn(Mono.just(expectedFriendShip));

        when(subscriptionReactiveDao.deleteBySubscriberIdAndTargetId(1, 2))
                .thenReturn(Mono.empty());

        // Invoke method

        BlockUpdateDTO.Request request = BlockUpdateDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.blockUpdates(request);

        // Verify

        Response expectedResponse = Response.builder()
                .success("true")
                .method(HttpMethod.POST)
                .message(String.format(String.format("{%s} blocks {%s} successfully.", email1, email2)))
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();

    }

    @Test
    public void testBlockUpdates_AlreadyBlocked() {
        // Prepare for data
        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User expectedUser1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User expectedUser2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Block expectedBlock = Block.builder()
                .blockId(1)
                .blockerId(1)
                .blockedId(2)
                .build();
        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(expectedUser1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(expectedUser2));

        when(friendshipReactiveDao.findByUserIdAndFriendId(1, 2))
                .thenReturn(Mono.empty());

        when(blockReactiveRepository.findByBlockerIdAndBlockedId(1, 2))
                .thenReturn(Mono.just(expectedBlock));

        // Invoke method

        BlockUpdateDTO.Request request = BlockUpdateDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.blockUpdates(request);

        // Verify

        Response expectedResponse = Response.builder()
                .success("true")
                .method(HttpMethod.POST)
                .message(String.format(String.format("{%s} already blocks {%s}.", email1, email2)))
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testBlockUpdates_NotAlreadyBlocked() {
        // Prepare for data
        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User expectedUser1 = User.builder()
                .userId(1)
                .email(email1)
                .build();
        User expectedUser2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        Block expectedBlock = Block.builder()
                .blockId(1)
                .blockerId(1)
                .blockedId(2)
                .build();
        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(expectedUser1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(expectedUser2));

        when(friendshipReactiveDao.findByUserIdAndFriendId(anyInt(), anyInt()))
                .thenReturn(Mono.empty());

        when(blockReactiveRepository.findByBlockerIdAndBlockedId(anyInt(), anyInt()))
                .thenReturn(Mono.empty());

        when(blockReactiveRepository.save(any()))
                .thenReturn(Mono.just(expectedBlock));
        // Invoke method

        BlockUpdateDTO.Request request = BlockUpdateDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.blockUpdates(request);

        // Verify
        Response expectedResponse = Response.builder()
                .success("true")
                .method(HttpMethod.POST)
                .message(String.format(String.format("{%s} blocks {%s} successfully.", email1, email2)))
                .build();


        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testBlockUpdates_NotFoundEmail1() {
        // Prepare for data
        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User expectedUser2 = User.builder()
                .userId(2)
                .email(email2)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.empty());

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.just(expectedUser2));

        // Invoke method

        BlockUpdateDTO.Request request = BlockUpdateDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.blockUpdates(request);

        // Verify
        Response expectedResponse = Response.builder()
                .method(HttpMethod.POST)
                .success("true")
                .message(String.format("Cannot find email {%s}. Please try another email", request.getEmail1()))
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testBlockUpdates_NotFoundEmail2() {
        // Prepare for data
        String email1 = "andy@example.com";
        String email2 = "john@example.com";

        User expectedUser1 = User.builder()
                .userId(1)
                .email(email1)
                .build();

        // Mock

        when(userReactiveDao.findByEmail(email1))
                .thenReturn(Mono.just(expectedUser1));

        when(userReactiveDao.findByEmail(email2))
                .thenReturn(Mono.empty());

        // Invoke method

        BlockUpdateDTO.Request request = BlockUpdateDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.blockUpdates(request);

        // Verify
        Response expectedResponse = Response.builder()
                .method(HttpMethod.POST)
                .success("true")
                .message(String.format("Cannot find email {%s}. Please try another email", request.getEmail2()))
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.OK).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }

    @Test
    public void testBlockUpdates_InValidEmail() {
        // Prepare for data
        String email1 = "andyexample.com";
        String email2 = "john@example.com";

        // Mock

        // Invoke method

        BlockUpdateDTO.Request request = BlockUpdateDTO.Request.builder()
                .email1(email1)
                .email2(email2)
                .build();

        Mono<ResponseEntity<Response>> actualResponseEntity = friendShipReactiveService.blockUpdates(request);

        // Verify
        Response expectedResponse = Response.builder()
                .message("Invalid email format {andyexample.com}. Please provide a valid email.")
                .method(HttpMethod.POST)
                .build();

        ResponseEntity<Response> expectResponseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(expectedResponse);

        StepVerifier.create(actualResponseEntity)
                .expectNext(expectResponseEntity)
                .verifyComplete();
    }
}
