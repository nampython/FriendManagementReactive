package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Data
@Builder
public class Response {
    //    @Builder.Default
    private String createAt;
    private HttpMethod method;
    private HttpStatus status;
    private String message;
    private String success;
    private Object result;

    public Response() {
    }

    public Response(String success, String message, Object result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public static class ResponseBuilder {
        private String success = "true";
        private HttpMethod method = HttpMethod.GET;
        private HttpStatus status = HttpStatus.OK;
    }
}
