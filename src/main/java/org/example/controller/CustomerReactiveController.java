package org.example.controller;

import org.example.model.CommonFriend;
import org.example.model.FriendConnection;
import org.example.model.FriendList;
import org.example.model.SubscribeToUpdates;
import org.example.model.friends.Friendship;
import org.example.model.friends.Subscription;
import org.example.model.friends.User;
import org.example.service.FriendShipReactiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CustomerReactiveController {
    private final FriendShipReactiveService friendShipReactiveService;
    private static final String GET_FRIENDS = "/v1/user/friends";
    private static final String GET_COMMON_FRIEND = "/v1/user/common";
    private static final String CREATE_FRIEND = "/v1/user/connect";
    @Autowired
    public CustomerReactiveController(FriendShipReactiveService friendShipReactiveService) {
        this.friendShipReactiveService = friendShipReactiveService;
    }

    /**
     * Take in an email and return a list of the friends
     * associated with the email passed in as a parameter.
     *
     * @param email Email that wants to get the friend list of this email
     * @return A flux&lt;string&gt; object
     */
    @GetMapping(value = GET_FRIENDS, produces = "application/json")
    public Mono<ResponseEntity<FriendList>> getFriendList(@RequestParam String email) {
        return friendShipReactiveService.getFriendsListByEmail(email);
    }


    /**
     * The getCommonFriends function takes in two email addresses and returns a list of their common friends.
     *
     * @param email1 Get the email address of user 1
     * @param email2 Specify the email of the second user

     *
     * @return A list of email addresses that are common to both friends
     */
    @GetMapping(value = GET_COMMON_FRIEND, produces = "application/json")
    public Mono<ResponseEntity<CommonFriend>> getCommonFriends(@RequestParam String email1, @RequestParam String email2) {
        return friendShipReactiveService.getCommonFriends(email1, email2);
    }


    /**
     *
     * @param email1
     * @param email2
     * @return
     */
    @PostMapping(value = CREATE_FRIEND, produces = "application/json")
    public Mono<ResponseEntity<FriendConnection>> createConnectionFriend(@RequestParam String email1, @RequestParam String email2) {
        return friendShipReactiveService.createFriendConnection(email1, email2);
    }


    /**
     * Subscribes the user with email address email2 to updates from the user with
     * email address email 1. Returns a Subscription object that contains information about this subscription.

     *
     * @param email1 email1 Pass the email of the user who wants to subscribe to updates
     * @param email2  email2 Specify the email of the user who is subscribing to updates
     *
     * @return A mono&lt;subscription&gt; object
     *
     */
    @PostMapping(value = "/user/subscribe")
    public Mono<ResponseEntity<SubscribeToUpdates>> subscribeToUpdates(@RequestParam String email1, @RequestParam String email2) {
        return friendShipReactiveService.subscribeToUpdates(email1, email2);
    }


    /**
     *
     * @param email1
     * @param email2
     * @return
     */
    @PostMapping(value = "/user/block")
    public Mono<Void> block(@RequestParam String email1, @RequestParam String email2) {
        return friendShipReactiveService.blockUpdates(email1, email2);
    }




}
