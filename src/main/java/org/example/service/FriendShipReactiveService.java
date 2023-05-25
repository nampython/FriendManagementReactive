package org.example.service;

import org.example.exception.InvalidEmailException;
import org.example.model.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FriendShipReactiveService {
    Mono<ResponseEntity<ResponseObject>> getFriendsListByEmail(String email) throws InvalidEmailException;
    Mono<ResponseEntity<ResponseObject>> getCommonFriends(String email1, String email2) throws InvalidEmailException;
    public Mono<ResponseEntity<ResponseObject>> createFriendConnection(String email1, String email2) throws InvalidEmailException;
    Mono<ResponseEntity<SubscribeToUpdates>> subscribeToUpdates(String subscriberEmail, String targetEmail) throws InvalidEmailException;
    Mono<Void> blockUpdates(String blockerEmail, String blockedEmail);
}
