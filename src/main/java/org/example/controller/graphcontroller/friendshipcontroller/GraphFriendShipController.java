package org.example.controller.graphcontroller.friendshipcontroller;

import org.example.dto.friendship.CommonFriendDTO;
import org.example.dto.friendship.FriendConnectionDTO;
import org.example.dto.friendship.FriendListDTO;
import org.example.model.Response;
import org.example.service.friendShipService.FriendShipReactiveService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class GraphFriendShipController {
    public static final String GET_COMMON_FRIENDS = "getCommonFriends";
    public static final String GET_FRIEND_LIST = "getFriendList";
    public static final String CREATE_CONNECTION_FRIEND = "createConnectionFriend";
    private final FriendShipReactiveService friendShipReactiveService;
    public GraphFriendShipController(FriendShipReactiveService friendShipReactiveService) {
        this.friendShipReactiveService = friendShipReactiveService;
    }

    /**
     * @see org.example.controller.reactiveController.FriendShipReactiveController#getFriendList(FriendListDTO.Request)
     */
    @MutationMapping(value = GET_FRIEND_LIST)
    public Mono<Response> getFriendList(@Argument FriendListDTO.Request request) {
        return getBody(friendShipReactiveService.getFriendsListByEmail(request));
    }

    /**
     * @see org.example.controller.reactiveController.FriendShipReactiveController#getCommonFriends(CommonFriendDTO.Request)
     */
    @MutationMapping(value = GET_COMMON_FRIENDS)
    public Mono<Response> getCommonFriends(@Argument CommonFriendDTO.Request request) {
        return getBody(friendShipReactiveService.getCommonFriends(request));
    }


    /**
     * @see org.example.controller.reactiveController.FriendShipReactiveController#createConnectionFriend(FriendConnectionDTO.Request)
     */
    @MutationMapping(value = CREATE_CONNECTION_FRIEND)
    public Mono<Response> createConnectionFriend(@Argument FriendConnectionDTO.Request request) {
        return getBody(friendShipReactiveService.createFriendConnection(request));
    }

    public <T> Mono<T> getBody(Mono<ResponseEntity<T>> responseEntityMono) {
        return responseEntityMono.mapNotNull(ResponseEntity::getBody);
    }
}
