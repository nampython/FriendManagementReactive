package org.example.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.regex.Pattern;


@Service
@Transactional
public class FriendShipReactiveServiceImpl implements FriendShipReactiveService {
    private static final String ACCEPTED = "accepted";
    private static final String PENDING = "pending";
    private static final String SUCCESS = "true";
    private static final String UNSUCCESS = "false";
    private static final String INVALID_EMAIL_EXCEPTION = "Invalid email format {%s}. Please provide a valid email.";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    private final UserReactiveDao userReactiveDao;
    private final FriendshipReactiveDao friendshipReactive;
    private final SubscriptionReactiveDao subscriptionReactiveDao;
    private final BlockReactiveRepository blockReactiveRepository;
    private final DatabaseClient r2dbcDatabaseClient;
    private final Logger logger = LoggerFactory.getLogger(FriendShipReactiveServiceImpl.class);

    @Autowired
    public FriendShipReactiveServiceImpl(UserReactiveDao userRepository, FriendshipReactiveDao friendshipRepositoryReactive, SubscriptionReactiveDao subscriptionReactiveDao, BlockReactiveRepository blockReactiveRepository, DatabaseClient r2dbcDatabaseClient) {
        this.userReactiveDao = userRepository;
        this.friendshipReactive = friendshipRepositoryReactive;
        this.subscriptionReactiveDao = subscriptionReactiveDao;
        this.blockReactiveRepository = blockReactiveRepository;
        this.r2dbcDatabaseClient = r2dbcDatabaseClient;
    }

    /**
     * Takes in an email address as a String and returns a list of the friends.
     *
     * @param email The email need to find the list of friends
     * @return A Mono&lt;ResponseEntity&lt;FriendList&gt;&gt;
     * @throws InvalidEmailException If the email is valid, throwing an InvalidEmailException
     */
    @Override
        public Mono<ResponseEntity<ResponseObject>> getFriendsListByEmail(String email) throws InvalidEmailException {
        try {
            logger.info("Message = {Processing getting the friend list by email: {}}", email);
            if (isValidEmail(email)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, email));
            } else {
                // Perform the remaining logic here to retrieve the friend list
                Mono<ResponseObject> friendListMono = userReactiveDao
                        .findByEmail(email).flux()
                        .concatMap(
                                user -> {
                                    logger.info("Message = {Getting the friendship list of user: {}}", user);
                                    return friendshipReactive.findByUserIdAndStatus(user.getUserId(), ACCEPTED);
                                }
                        )
                        .concatMap(
                                friendship -> {
                                    logger.info("Message = {Getting the user from friend id: {}}", friendship.getFriendshipId());
                                    return userReactiveDao.findByUserId(friendship.getFriendshipId());
                                }
                        )
                        .map(user -> {
                            logger.info("Message = {Getting the email from user: {}}", user);
                            return user.getEmail();
                        })
                        .collectList()
                        .map(
                                emails -> {
                                    FriendList friendList = new FriendList();
                                    friendList.setFriends(emails);
                                    friendList.setCount(emails.size());
                                    return friendList;
                                }
                        ).map(
                                friendList -> {
                                    logger.info("Message = {The friend list retrieved successfully.}");
                                    ResponseObject responseObject = new ResponseObject();
                                    responseObject.setMessage("Friend list retrieved successfully.");
                                    responseObject.setSuccess(SUCCESS);
                                    responseObject.setResult(friendList);
                                    return responseObject;
                                }
                        );
                return friendListMono.map(friendList -> ResponseEntity.ok().body(friendList));
            }
        } catch (InvalidEmailException ex) {
            logger.error("Message = {Error while getting the friend list by email: {}}", email, ex);
            ResponseObject responseObject = new ResponseObject();
            responseObject.setMessage(ex.getMessage());
            return Mono.just(ResponseEntity.badRequest().body(responseObject));
        }
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
    private boolean isValidEmail(String email) {
        return !Boolean.TRUE.equals(Mono.fromSupplier(
                () -> EMAIL_PATTERN.matcher(email).matches()
        ).block());
    }


    /**
     * Takes in two email addresses and returns a list of their common friends.
     *
     * @param email1 The first email that wants to find the common list friend
     * @param email2 The second email that wants to find the common list friend
     * @return A list of email addresses that are common friends to both users
     * @throws InvalidEmailException When an email is invalid, throw an exception
     */
    @Override
    public Mono<ResponseEntity<CommonFriend>> getCommonFriends(String email1, String email2) throws InvalidEmailException {
        try {
            logger.info("Message = {Processing getting the common friend list by email: {} and {}}", email1, email2);
            if (isValidEmail(email1)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, email1));
            } else if (isValidEmail(email2)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, email2));
            } else {
                Mono<User> user1Mono = userReactiveDao.findByEmail(email1);
                Mono<User> user2Mono = userReactiveDao.findByEmail(email2);
                Flux<CommonFriend> commonFriendFlux = user1Mono.flatMapMany(user1 ->
                        user2Mono.flatMapMany(user2 ->
                                friendshipReactive.findByUserIdAndStatus(user1.getUserId(), "accepted")
                                        .concatMap(friendship1 ->
                                                friendshipReactive.findByUserIdAndStatus(user2.getUserId(), "accepted")
                                                        .filter(friendship2 -> friendship1.getFriendId() == friendship2.getFriendId())
                                                        .concatMap(friendship2 ->
                                                                userReactiveDao.findByUserId(friendship2.getFriendId())
                                                                        .map(User::getEmail)
                                                                        .flux()
                                                                        .collectList()
                                                                        .map(emails -> {
                                                                            CommonFriend commonFriend = new CommonFriend();
                                                                            commonFriend.setFriends(emails);
                                                                            commonFriend.setSuccess(SUCCESS);
                                                                            commonFriend.setCount(emails.size());
                                                                            commonFriend.setMessage("Common Friend list retrieved successfully.");
                                                                            return commonFriend;
                                                                        })
                                                        )
                                        )
                        )
                );
                return commonFriendFlux
                        .collectList()
                        .map(
                                commonFriends -> ResponseEntity.ok().body(commonFriends.get(0))
                        );
            }
        } catch (InvalidEmailException ex) {
            logger.error("Message = {Error while getting the common list friends {} and {}", email1, email2, ex);
            CommonFriend commonFriend = new CommonFriend();
            commonFriend.setMessage(ex.getMessage());
            return Mono.just(ResponseEntity.badRequest().body(commonFriend));
        }
    }

    /**
     * Establishes a friend connection between two email addresses.
     *
     * @param email1 The first email wants to make the friend connection
     * @param email2 The second email wants to make the friend connection
     * @return A mono&lt;friendship&gt; object
     */
    @Override
    public Mono<ResponseEntity<FriendConnection>> createFriendConnection(String email1, String email2) {
        try {
            logger.info("Message = {Processing creating the friend connection by email: {} and {}}", email1, email2);
            if (isValidEmail(email1)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, email1));
            } else if (isValidEmail(email2)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, email2));
            } else {
                return userReactiveDao.findByEmail(email1)
                        .flux()
                        .concatMap(user1 -> userReactiveDao.findByEmail(email2)
                                .flatMap(user2 -> {
                                    int userId1 = user1.getUserId();
                                    int userId2 = user2.getUserId();

                                    return friendshipReactive.findByUserIdAndFriendId(userId1, userId2)
                                            .switchIfEmpty(
                                                    Mono.defer(() -> {
                                                        Friendship friendship = new Friendship();
                                                        friendship.setUserId(userId1);
                                                        friendship.setFriendId(userId2);
                                                        friendship.setStatus("accepted");
                                                        return friendshipReactive.save(friendship);
                                                    })
                                            )
                                            .flatMap(savedFriendship -> {
                                                FriendConnection friendConnection = new FriendConnection();
                                                friendConnection.setFriendship(savedFriendship);
                                                friendConnection.setSuccess(SUCCESS);
                                                friendConnection.setMessage("Established the connection successfully.");
                                                return Mono.just(friendConnection);
                                            });
                                })
                        )
                        .next() // Get the first (and only) element from the Flux
                        .map(friendConnection -> ResponseEntity.ok(friendConnection))
                        .defaultIfEmpty(ResponseEntity.notFound().build());
            }
        } catch (InvalidEmailException ex) {
            logger.error("Message = {Error while creating the friend connection {} and {}", email1, email2, ex);
            FriendConnection friendConnection = new FriendConnection();
            friendConnection.setMessage(ex.getMessage());
            return Mono.just(ResponseEntity.badRequest().body(friendConnection));
        }
    }


    /**
     * Subscribe to updates from an email address.
     *
     * @param subscriberEmail the subscriber email address.
     * @param targetEmail     the target email address to subscribe to.
     * @return A Mono&lt;ResponseEntity&lt;SubscribeToUpdates&gt;&gt; object
     */
    @Override
    public Mono<ResponseEntity<SubscribeToUpdates>> subscribeToUpdates(String subscriberEmail, String targetEmail) throws InvalidEmailException {
        //TODO
        // Need to check some conditions below
        // Error: "Email is not valid" [Re-check]
        // Error: "One or both email addresses were not found." [Re-check]
        // Error: "They already have a subscription" [Re-check]
        // Error: "subscribeTo and target email addresses cannot be the same." [Re-check]
        try {
            if (isValidEmail(subscriberEmail)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, subscriberEmail));
            } else if (isValidEmail(targetEmail)) {
                throw new InvalidEmailException(String.format(INVALID_EMAIL_EXCEPTION, targetEmail));
            } else {
                Flux<User> subscriberUser = userReactiveDao.findByEmail(subscriberEmail).flux();
                Flux<User> targetUser = userReactiveDao.findByEmail(targetEmail).flux();
                return subscriberUser
                        .concatMap(subscriber -> targetUser
                                .concatMap(target -> {
                                    // Check if the subscription already exists
                                    return subscriptionReactiveDao.findBySubscriberIdAndTargetId(subscriber.getUserId(), target.getUserId())
                                            .flatMap(existingSubscription -> {
                                                SubscribeToUpdates subscribeToUpdates = new SubscribeToUpdates();
                                                subscribeToUpdates.setMessage("They already have a subscription.");
                                                return Mono.just(ResponseEntity.ok().body(subscribeToUpdates));
                                            })
                                            .switchIfEmpty(
                                                    // Create a new subscription
                                                    subscriptionReactiveDao.save(new Subscription(subscriber.getUserId(), target.getUserId()))
                                                            .map(savedSubscription -> {
                                                                SubscribeToUpdates subscribeToUpdates = new SubscribeToUpdates();
                                                                subscribeToUpdates.setSubscription(savedSubscription);
                                                                subscribeToUpdates.setSuccess(SUCCESS);
                                                                subscribeToUpdates.setMessage("Subscribed successfully.");
                                                                return ResponseEntity.ok().body(subscribeToUpdates);
                                                            })
                                            );
                                })
                                // If the target user is not found
                                .switchIfEmpty(Mono.defer(() -> {
                                    SubscribeToUpdates subscribeToUpdates = new SubscribeToUpdates();
                                    subscribeToUpdates.setMessage("Target user not found.");
                                    return Mono.just(ResponseEntity.badRequest().body(subscribeToUpdates));
                                }))
                        )
                        // If the subscriber user is not found
                        .switchIfEmpty(Mono.defer(() -> {
                            SubscribeToUpdates subscribeToUpdates = new SubscribeToUpdates();
                            subscribeToUpdates.setMessage("Subscriber user not found.");
                            return Mono.just(ResponseEntity.badRequest().body(subscribeToUpdates));
                        }))
                        .single(); // Convert the Flux to a Mono
            }
        } catch (InvalidEmailException ex) {
            logger.error("Error while subscribing to updates {} and {}", subscriberEmail, targetEmail, ex);
            SubscribeToUpdates subscribeToUpdates = new SubscribeToUpdates();
            subscribeToUpdates.setMessage(ex.getMessage());
            return Mono.just(ResponseEntity.badRequest().body(subscribeToUpdates));
        }
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
}
