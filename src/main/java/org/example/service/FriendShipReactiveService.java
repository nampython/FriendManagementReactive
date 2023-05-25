package org.example.service;

import org.example.exception.InvalidEmailException;
import org.example.model.CommonFriend;
import org.example.model.FriendConnection;
import org.example.model.FriendList;
import org.example.model.SubscribeToUpdates;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface FriendShipReactiveService {
    Mono<ResponseEntity<FriendList>> getFriendsListByEmail(String email) throws InvalidEmailException;
    Mono<ResponseEntity<CommonFriend>> getCommonFriends(String email1, String email2) throws InvalidEmailException;
    Mono<ResponseEntity<FriendConnection>> createFriendConnection(String email1, String email2) throws InvalidEmailException;
    Mono<ResponseEntity<SubscribeToUpdates>> subscribeToUpdates(String subscriberEmail, String targetEmail) throws InvalidEmailException;
    Mono<Void> blockUpdates(String blockerEmail, String blockedEmail);
}
