package org.example.service;

import org.example.dto.CommonFriendDTO;
import org.example.dto.EligibleEmailAddressesDTO;
import org.example.dto.FriendConnection;
import org.example.dto.FriendListDTO;
import org.example.dto.SubscribeUpdatesDTO;
import org.example.exception.InvalidEmailException;
import org.example.model.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FriendShipReactiveService {
    Mono<ResponseEntity<Response>> getFriendsListByEmail(FriendListDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> getCommonFriends(CommonFriendDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> createFriendConnection(FriendConnection.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> subscribeToUpdates(SubscribeUpdatesDTO.Request request) throws InvalidEmailException;
    Mono<Void> blockUpdates(String blockerEmail, String blockedEmail) throws InvalidEmailException;
    Mono<ResponseEntity<Response>> getEligibleEmailAddresses(EligibleEmailAddressesDTO.Request request) throws InvalidEmailException;
}
