package org.example.controller;

import org.example.dto.CommonFriendDTO;
import org.example.dto.EligibleEmailAddressesDTO;
import org.example.dto.FriendConnection;
import org.example.dto.FriendListDTO;
import org.example.dto.SubscribeUpdatesDTO;
import org.example.model.*;
import org.example.service.FriendShipReactiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class CustomerReactiveController {
    private final FriendShipReactiveService friendShipReactiveService;
    private static final String VERSION_API = "/v1";
    private static final String GET_FRIENDS = VERSION_API +  "/friends";
    private static final String GET_COMMON_FRIEND = VERSION_API+ "/user/common";
    private static final String CREATE_FRIEND = VERSION_API + "/user/connect";
    private static final String GET_UPDATE_EMAIL = VERSION_API + "/user/updatable";
    private static final String SUBSCRIBE_TO_UPDATE = VERSION_API + "/user/subscribe";
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
     * Used to create a friend connection between two email addresses.
     *
     * @param request Contain 2 emails that want to make the connection
     *
     * @return A Mono&lt;ResponseEntity&lt;ResponseObject&gt&gt;
     */
    @PostMapping(value = CREATE_FRIEND, produces = "application/json")
    public Mono<ResponseEntity<ResponseObject>> createConnectionFriend(@RequestBody FriendConnection.Request request) {
        return friendShipReactiveService.createFriendConnection(request);
    }


    /**
     * Used to make a subscription from 2 emails.
     * @param request contain 2 emails from request to subscribe from email1 to email2
     *
     * @return A Mono&lt;ResponseEntity&lt;ResponseObject&gt;&gt;
     *
     */
    @PostMapping(value = SUBSCRIBE_TO_UPDATE)
    public Mono<ResponseEntity<ResponseObject>> subscribeToUpdates(@RequestBody SubscribeUpdatesDTO.Request request) {
        return friendShipReactiveService.subscribeToUpdates(request);
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
     * @param request contain the required email to retrieve all email addresses
     *
     * @return A Mono&lt;ResponseEntity&lt;ResponseObject&gt;&gt;
     *
     */
    @GetMapping(value = GET_UPDATE_EMAIL, produces = "application/json")
    public Mono<ResponseEntity<ResponseObject>> getEligibleEmailAddresses(@RequestBody EligibleEmailAddressesDTO.Request request) {
        return friendShipReactiveService.getEligibleEmailAddresses(request);
    }
}
