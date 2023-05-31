package org.example.controller;

import org.example.dto.CommonFriendDTO;
import org.example.dto.FriendListDTO;
import org.example.model.*;
import org.example.service.FriendShipReactiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class CustomerReactiveController {
    private final FriendShipReactiveService friendShipReactiveService;
    private static final String GET_FRIENDS = "/v1/user/friends";
    private static final String GET_COMMON_FRIEND = "/v1/user/common";
    private static final String CREATE_FRIEND = "/v1/user/connect";
    private static final String GET_UPDATE_EMAIL = "/v1/user/updatable";
    private static final String SUBSCRIBE_TO_UPDATE = "/v1/user/subscribe";
    @Autowired
    public CustomerReactiveController(FriendShipReactiveService friendShipReactiveService) {
        this.friendShipReactiveService = friendShipReactiveService;
    }

    /**
     * Take in an email and return a list of the friends
     * associated with the email passed in as a parameter.
     *
     * @param request Email that wants to get the friend list of this email
     * @return A flux&lt;string&gt; object
     */
    @GetMapping(value = GET_FRIENDS, produces = "application/json")
    public Mono<ResponseEntity<ResponseObject>> getFriendList(@RequestBody FriendListDTO.Request request) {
        return friendShipReactiveService.getFriendsListByEmail(request);
    }


    /**
     * Takes in two email addresses and returns a list of their common friends.
     *
     * @param request Contain 2 emails to find the common list of friends
     *
     * @return A list of email addresses that are common to both friends
     */
    @GetMapping(value = GET_COMMON_FRIEND, produces = "application/json")
    public Mono<ResponseEntity<ResponseObject>> getCommonFriends(@RequestBody CommonFriendDTO.Request request) {
        return friendShipReactiveService.getCommonFriends(request);
    }


    /**
     *
     * @param email1
     * @param email2
     * @return
     */
    @PostMapping(value = CREATE_FRIEND, produces = "application/json")
    public Mono<ResponseEntity<ResponseObject>> createConnectionFriend(@RequestParam String email1, @RequestParam String email2) {
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
    @PostMapping(value = SUBSCRIBE_TO_UPDATE)
    public Mono<ResponseEntity<ResponseObject>> subscribeToUpdates(@RequestParam String email1, @RequestParam String email2) {
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

    /**
     * Retrieves all email addresses that can receive updates from an email address.
     *
     * @param email Retrieve the sender's email address from the database
     *
     * @return A Mono&lt;ResponseEntity&lt;ResponseObject&gt;&gt;
     *
     */
    @GetMapping(GET_UPDATE_EMAIL)
    public Mono<ResponseEntity<ResponseObject>> getEligibleEmailAddresses(@RequestParam String email) {
        return friendShipReactiveService.getEligibleEmailAddresses(email);
    }
}
