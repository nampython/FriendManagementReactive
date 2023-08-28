package org.example.service.friendShipService;

import org.example.dto.friendship.*;
import org.example.exception.InvalidEmailException;
import org.example.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface FriendShipReactiveService {
    Mono<ResponseEntity<Response>> getFriendsListByEmail(FriendListDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> getCommonFriends(CommonFriendDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> createFriendConnection(FriendConnectionDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> subscribeToUpdates(SubscribeUpdatesDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> blockUpdates(@RequestBody BlockUpdateDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> getEligibleEmailAddresses(EligibleEmailAddressesDTO.Request request) throws InvalidEmailException;
}
