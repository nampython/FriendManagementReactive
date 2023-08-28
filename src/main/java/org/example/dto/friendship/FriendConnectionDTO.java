package org.example.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.friends.Friendship;


public interface FriendConnectionDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    class Request {
        private String email1;
        private String email2;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    class Response {
        private Friendship friendship;
    }
}
