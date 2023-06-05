package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface FriendListDTO {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    class Request {
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    class Response {
        private List<String> friends = null;
        private Integer count = null;
    }
}
