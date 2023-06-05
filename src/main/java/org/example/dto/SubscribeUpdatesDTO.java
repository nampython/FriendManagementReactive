package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.friends.Subscription;

public interface SubscribeUpdatesDTO {
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
        private Subscription subscription = null;
    }
}
