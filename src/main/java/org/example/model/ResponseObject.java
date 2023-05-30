package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseObject {
    private String timestamp = String.valueOf(System.currentTimeMillis());
    private String message;
    private String success = "false";
    private Object result;

    public ResponseObject(String success, String s, Object result) {
        this.success = success;
        this.message = s;
        this.result = result;
    }
}
