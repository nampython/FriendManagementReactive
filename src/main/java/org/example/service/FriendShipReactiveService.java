package org.example.service;

import org.example.dto.CommonFriendDTO;
import org.example.dto.FriendConnection;
import org.example.dto.FriendListDTO;
import org.example.exception.InvalidEmailException;
import org.example.model.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FriendShipReactiveService {
    Mono<ResponseEntity<ResponseObject>> getFriendsListByEmail(FriendListDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<ResponseObject>> getCommonFriends(CommonFriendDTO.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<ResponseObject>> createFriendConnection(FriendConnection.Request request) throws InvalidEmailException;
    Mono<ResponseEntity<ResponseObject>> subscribeToUpdates(String subscriberEmail, String targetEmail) throws InvalidEmailException;
    Mono<Void> blockUpdates(String blockerEmail, String blockedEmail) throws InvalidEmailException;
    Mono<ResponseEntity<ResponseObject>> getEligibleEmailAddresses(String senderEmail) throws InvalidEmailException;
}
