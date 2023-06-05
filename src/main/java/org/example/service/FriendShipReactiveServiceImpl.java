package org.example.service;

import org.example.dto.CommonFriendDTO;
import org.example.dto.EligibleEmailAddressesDTO;
import org.example.dto.FriendConnection;
import org.example.dto.FriendListDTO;
import org.example.dto.SubscribeUpdatesDTO;
import org.example.exception.InvalidEmailException;
import org.example.model.*;
import org.example.model.friends.Block;
import org.example.model.friends.Friendship;
import org.example.model.friends.Subscription;
import org.example.model.friends.User;
import org.example.repository.BlockReactiveRepository;
import org.example.repository.FriendshipReactiveDao;
import org.example.repository.SubscriptionReactiveDao;
import org.example.repository.UserReactiveDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Transactional
public class FriendShipReactiveServiceImpl implements FriendShipReactiveService {
    private static final String ACCEPTED;
    private static final String PENDING;
    private static final String SUCCESS;
    private static final String UNSUCCESS;
    private static final String GET_FRIEND_LIST_SUCCESSFULLY;
    private static final String GET_COMMON_FRIEND_LIST;
    private static final String ALREADY_FRIEND;
    private static final String SUCCESSFULLY_ESTABLISH_FRIEND;
    private static final String INVALID_EMAIL_EXCEPTION;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    private static final String EMAIL_NOT_FOUND = "Cannot find email {%s}. Please try another email";

    // Signal message
    static {
        SUCCESS = "true";
        UNSUCCESS = "false";
        PENDING = "pending";
        ACCEPTED = "accepted";
    }

    // Display Message
    static {
        ALREADY_FRIEND = "%s and %s are already friends. There is no need to create a new friend connection.";
        GET_FRIEND_LIST_SUCCESSFULLY = "Friend list retrieved successfully.";
        GET_COMMON_FRIEND_LIST = "Common Friend list retrieved successfully.";
        SUCCESSFULLY_ESTABLISH_FRIEND = "The connection is established successfully.";
    }

    // Exception message
    static {
        INVALID_EMAIL_EXCEPTION = "Invalid email format {%s}. Please provide a valid email.";
    }

    private final UserReactiveDao userReactiveDao;
    private final FriendshipReactiveDao friendshipReactive;
    private final SubscriptionReactiveDao subscriptionReactiveDao;
    private final BlockReactiveRepository blockReactiveRepository;

    @Autowired
    public FriendShipReactiveServiceImpl(UserReactiveDao userRepository, FriendshipReactiveDao friendshipRepositoryReactive, SubscriptionReactiveDao subscriptionReactiveDao, BlockReactiveRepository blockReactiveRepository, DatabaseClient r2dbcDatabaseClient) {
        this.userReactiveDao = userRepository;
        this.friendshipReactive = friendshipRepositoryReactive;
        this.subscriptionReactiveDao = subscriptionReactiveDao;
        this.blockReactiveRepository = blockReactiveRepository;
    }

    /**
     * Takes a request object contain email that retrieves the list of friends for this email
     *
     * @param request The email need to find the list of friends
     * @return A Mono&lt;ResponseEntity&lt;FriendList&gt;&gt;
     * @throws InvalidEmailException If the email is valid, throwing an InvalidEmailException
     */
    @Override
    public Mono<ResponseEntity<Response>> getFriendsListByEmail(FriendListDTO.Request request) throws InvalidEmailException {
        return Mono.just(request.getEmail())
                // to switch to the blocking context
//                .publishOn(Schedulers.boundedElastic())
                .filter(this::isValidEmail)
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail()))))
                .flatMap(email -> userReactiveDao.findByEmail(email).flux()
                        .concatMap(user -> friendshipReactive.findByUserIdAndStatus(user.getUserId(), ACCEPTED))
                        .concatMap(friendship -> userReactiveDao.findByUserId(friendship.getFriendshipId()))
                        .map(User::getEmail)
                        .collectList()
                        .map(emails -> FriendListDTO.Response
                                .builder()
                                .friends(emails)
                                .count(emails.size())
                                .build()
                        )
                        .map(friendList -> ResponseEntity.status(HttpStatus.OK).body(
                                Response.builder()
                                        .method(HttpMethod.POST)
                                        .message(GET_FRIEND_LIST_SUCCESSFULLY)
                                        .result(friendList)
                                        .build()
                        )))
                .log()
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Response.builder()
                                .method(HttpMethod.POST)
                                .message(ex.getMessage())
                                .build()))
                );
    }

    /**
     * Implement your email validation logic here and return true if the email is valid, false otherwise.
     * Use regular expressions or any other validation mechanism.
     * Email addresses that will be valid via this email validation technique are:
     * <ul>
     *     <li>username@domain.com</li>
     *     <li>user.name@domain.com</li>
     *     <li>user-name@domain.com</li>
     *     <li>username@domain.com</li>
     *     <li>username@domain.co.in</li>
     *     <li>user_name@domain.com</li>
     * </ul>
     * Email addresses that will be invalid via this email validation:
     * <ul>
     *     <li>username.@domain.com</li>
     *     <li>.user.name@domain.com</li>
     *     <li>user-name@domain.com.</li>
     *     <li>username@.com</li>
     * </ul>
     *
     * @param email Check if the email is valid;
     * @return True if email is valid and contrast
     */
    public boolean isValidEmail(String email) {
        return Boolean.TRUE.equals(EMAIL_PATTERN.matcher(email).matches());
    }


    /**
     * Takes in two email addresses and returns a list of their common friends.
     *
     * @param request Contain the email fields that want to find the common list friend
     * @return A list of email addresses that are common friends to both users
     * @throws InvalidEmailException When an email is invalid, throw an exception
     */
    @Override
    public Mono<ResponseEntity<Response>> getCommonFriends(CommonFriendDTO.Request request) throws InvalidEmailException {

        return Mono.just(request)
                //Possibly blocking call in non-blocking context could lead to thread starvation
//                .publishOn(Schedulers.boundedElastic())
                .filter(email1 -> isValidEmail(request.getEmail1()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail1()))))
                .filter(email2 -> isValidEmail(request.getEmail2()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail2()))))
                .flatMap(req -> {
                    Mono<User> user1 = userReactiveDao.findByEmail(req.getEmail1());
                    Mono<User> user2 = userReactiveDao.findByEmail(req.getEmail2());
                    // Perform the remaining logic here to retrieve the common friend list
                    Flux<Response> response = user1.flatMapMany(u1 ->
                                    user2.flatMapMany(u2 ->
                                                    friendshipReactive.findByUserIdAndStatus(u1.getUserId(), ACCEPTED)
                                                            .concatMap(friendship1 ->
                                                                    friendshipReactive.findByUserIdAndStatus(u2.getUserId(), ACCEPTED)
                                                                            .filter(friendship2 -> Objects.equals(friendship1.getFriendId(), friendship2.getFriendId()))
                                                                            .concatMap(friendship2 ->
                                                                                    userReactiveDao.findByUserId(friendship2.getFriendId())
                                                                                            .map(User::getEmail)
                                                                                            .flux()
                                                                                            .collectList()
                                                                                            .map(
                                                                                                    emails -> CommonFriendDTO.Response.builder()
                                                                                                            .friends(emails)
                                                                                                            .count(emails.size())
                                                                                                            .build())
                                                                                            .map(
                                                                                                    commonFriend -> Response.builder()
                                                                                                            .method(HttpMethod.POST)
                                                                                                            .message(GET_COMMON_FRIEND_LIST)
                                                                                                            .result(commonFriend)
                                                                                                            .build()
                                                                                            )
                                                                            )
                                                            ))// In case of not founding user of email 2
                                            .switchIfEmpty(Mono.defer(
                                                    () -> Mono.just(
                                                            Response.builder()
                                                                    .method(HttpMethod.POST)
                                                                    .message(String.format(EMAIL_NOT_FOUND, req.getEmail2()))
                                                                    .build()
                                                    )
                                            ))
                            ) // In case of not founding user of email 1
                            .switchIfEmpty(Mono.defer(
                                    () -> Mono.just(Response.builder()
                                            .method(HttpMethod.POST)
                                            .message(String.format(EMAIL_NOT_FOUND, req.getEmail1()))
                                            .build()
                                    ))
                            );
                    return response.next() // Get the first (and only) element from the Flux
                            .map(respObj -> ResponseEntity.status(HttpStatus.OK).body(respObj));
                })
                // In case of email is invalid, return normally ResponseEntity with a message from a throwable object
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Response.builder()
                                .method(HttpMethod.POST)
                                .message(ex.getMessage())
                                .build())));
    }

    /**
     * Establishes a friend connection between two email addresses.
     *
     * @param request Contain 2 emails that want to make a connection.
     * @return A Mono&lt;ResponseEntity&lt;ResponseObject&gt&gt;
     */
    @Override
    public Mono<ResponseEntity<Response>> createFriendConnection(FriendConnection.Request request) {

        return Mono.just(request)
                .filter(email1 -> isValidEmail(request.getEmail1()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail1()))))
                .filter(email2 -> isValidEmail(request.getEmail2()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail2()))))
                .flatMap(req -> {
                    // Perform the remaining logic here to create the connection bettwen 2 email
                    Mono<User> user1 = userReactiveDao.findByEmail(req.getEmail1());
                    Flux<Response> response = user1.flux()
                            .concatMap(u1 -> {
                                        Mono<User> user2 = userReactiveDao.findByEmail(req.getEmail2());
                                        return user2
                                                .flatMap(u2 -> {
                                                    int userId1 = u1.getUserId();
                                                    int userId2 = u2.getUserId();

                                                    return friendshipReactive.findByUserIdAndFriendId(userId1, userId2)
                                                            .flatMap(existingFriendship -> Mono.just(Response.builder()
                                                                    .result(null)
                                                                    // TODO: Need to split email by remove @ and get the name before @
                                                                    .message(String.format(ALREADY_FRIEND, request.getEmail1(), request.getEmail2()))
                                                                    .method(HttpMethod.POST)
                                                                    .build()))
                                                            .switchIfEmpty(
                                                                    Mono.defer(() -> friendshipReactive.save(
                                                                            Friendship.builder()
                                                                                    .userId(userId1)
                                                                                    .friendId(userId2)
                                                                                    .status(ACCEPTED)
                                                                                    .build()
                                                                    )).map(
                                                                            friendship -> Response.builder()
                                                                                    .result(friendship)
                                                                                    .success(SUCCESS)
                                                                                    .message(SUCCESSFULLY_ESTABLISH_FRIEND)
                                                                                    .method(HttpMethod.POST)
                                                                                    .build()
                                                                    )
                                                            );
                                                })
                                                // In case of not founding user of email 2
                                                .switchIfEmpty(
                                                        Mono.defer(() -> Mono.just(Response.builder()
                                                                .success(SUCCESS)
                                                                .method(HttpMethod.POST)
                                                                .message(String.format(EMAIL_NOT_FOUND, req.getEmail2()))
                                                                .build()))
                                                );
                                    }
                            )// In case of not founding user of email 1
                            .switchIfEmpty(
                                    Mono.defer(() -> Mono.just(Response.builder()
                                            .success(SUCCESS)
                                            .method(HttpMethod.POST)
                                            .message(String.format(EMAIL_NOT_FOUND, req.getEmail2()))
                                            .build()))
                            );
                    return response.next() // Get the first (and only) element from the Flux
                            .map(resObject -> ResponseEntity.status(HttpStatus.OK).body(resObject));
                })
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Response.builder()
                                .message(ex.getMessage())
                                .build())));
    }

    public Mono<ResponseEntity<Response>> subscribeToUpdates(SubscribeUpdatesDTO.Request request) throws InvalidEmailException {

        return Mono.just(request)
                .filter(email1 -> isValidEmail(request.getEmail1()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail1()))))
                .filter(email2 -> isValidEmail(request.getEmail2()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail2()))))
                .flatMap(req -> {
                    Flux<User> subscriberUser = userReactiveDao.findByEmail(req.getEmail1()).flux();
                    Flux<User> targetUser = userReactiveDao.findByEmail(req.getEmail2()).flux();
                    Flux<Response> response = subscriberUser
                            .concatMap(subscriber -> targetUser
                                    .concatMap(target -> {
                                        // Check if the subscription already exists
                                        return subscriptionReactiveDao.findBySubscriberIdAndTargetId(subscriber.getUserId(), target.getUserId())
                                                .flatMap(existingSubscription -> Mono.just(Response.builder()
                                                                .success(SUCCESS)
                                                                .result(existingSubscription)
                                                                .message("They already have a subscription.")
                                                                .build()
                                                        )
                                                )
                                                .switchIfEmpty(
                                                        // Create a new subscription
                                                        subscriptionReactiveDao.save(new Subscription(subscriber.getUserId(), target.getUserId()))
                                                                .map(
                                                                        savedSubscription -> SubscribeUpdatesDTO.Response.builder()
                                                                                .subscription(savedSubscription)
                                                                                .build()
                                                                )
                                                                .map(
                                                                        savedSubscription -> Response.builder()
                                                                                .message("Subscribed successfully.")
                                                                                .result(savedSubscription)
                                                                                .success(SUCCESS)
                                                                                .build()
                                                                )
                                                );
                                    })
                                    // If the target user is not found
                                    .switchIfEmpty(
                                            Mono.defer(() -> Mono.just(Response.builder()
                                                    .success(SUCCESS)
                                                    .message("Target user not found.")
                                                    .build()))
                                    )
                            )
                            // If the subscriber user is not found
                            .switchIfEmpty(
                                    Mono.defer(() -> Mono.just(Response.builder()
                                            .success(SUCCESS)
                                            .message("Subscriber user not found.")
                                            .build()))
                            );
                    return response.next() // Get the first (and only) element from the Flux
                            .map(resObject -> ResponseEntity.status(HttpStatus.OK).body(resObject));
                })
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Response.builder()
                                .message(ex.getMessage())
                                .build()))

                );
    }


    /**
     * Block updates from an email address.
     *
     * @param blockerEmail The blocker email address.
     * @param blockedEmail The blocked email address.
     * @return
     */
    @Override
    public Mono<Void> blockUpdates(String blockerEmail, String blockedEmail) {
        Mono<User> blockerUserMono = userReactiveDao.findByEmail(blockerEmail);
        Mono<User> blockedUserMono = userReactiveDao.findByEmail(blockedEmail);

        return Flux.concat(blockerUserMono.flux(), blockedUserMono.flux())
                .collectList()
                .flatMap(users -> {
                    User blockerUser = users.get(0);
                    User blockedUser = users.get(1);

                    Mono<Friendship> friendshipMono = friendshipReactive
                            .findByUserIdAndFriendId(blockerUser.getUserId(), blockedUser.getUserId());
                    return friendshipMono.flatMap(friendship -> {
                        // They are friends, delete the subscription
                        return subscriptionReactiveDao
                                .deleteBySubscriberIdAndTargetId(blockerUser.getUserId(), blockedUser.getUserId())
                                .then();
                    }).switchIfEmpty(Mono.defer(() -> {
                        // They are not friends, add to the block table
                        Block block = new Block(blockerUser.getUserId(), blockedUser.getUserId());
                        return blockReactiveRepository.save(block).then();
                    }));
                });
    }


    /**
     * Retrieves all email addresses that can receive updates from an email address.
     *
     * @param request contain email that wants to receive the update
     * @return A Mono&lt;ResponseEntity&lt;ResponseObject&gt;&gt;
     */
    @Override
    public Mono<ResponseEntity<Response>> getEligibleEmailAddresses(EligibleEmailAddressesDTO.Request request) throws InvalidEmailException {

        return Mono.just(request)
                .filter(email -> isValidEmail(request.getEmail()))
                .switchIfEmpty(Mono.error(new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, request.getEmail()))))
                .flatMap(req -> {
                            Mono<Response> response = userReactiveDao.findByEmail(req.getEmail())
                                    .flatMapMany(senderUser -> Flux.concat(
                                            friendshipReactive.findByUserId(senderUser.getUserId())
                                                    .map(Friendship::getFriendId),
                                            subscriptionReactiveDao.findBySubscriberId(senderUser.getUserId())
                                                    .map(Subscription::getTargetId)
                                    ))
                                    .distinct()
                                    .publishOn(Schedulers.boundedElastic())
                                    .filter(
                                            targetUserId -> blockReactiveRepository
                                                    .findByBlockerId(targetUserId).blockOptional().isEmpty()
                                    )
                                    .flatMap(
                                            userReactiveDao::findByUserId
                                    )
                                    .map(User::getEmail)
                                    .collectList()
                                    .map(emails -> EligibleEmailAddressesDTO.Response
                                            .builder()
                                            .friends(emails)
                                            .count(emails.size())
                                            .build())
                                    .map(emails -> {
                                        Response responseObject = new Response();
                                        responseObject.setMessage("Friend list retrieved successfully.");
                                        responseObject.setSuccess(SUCCESS);
                                        responseObject.setResult(emails);
                                        return responseObject;
                                    });
                            return response.map(
                                    emails -> ResponseEntity.status(HttpStatus.OK).body(emails)
                            );
                        }
                )
                .onErrorResume(ex -> {
                            ;
                            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                    Response.builder()
                                            .message(ex.getMessage())
                                            .build()));
                        }
                );
    }
}
