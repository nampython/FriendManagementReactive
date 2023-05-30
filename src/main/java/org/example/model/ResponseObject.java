package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseObject {
    private String timestamp = String.valueOf(System.currentTimeMillis());
//    private int status;
    private String message;
    private String success = "false";
    private Object result;

    public ResponseObject(String success, String s, Object result) {
        this.success = success;
        this.message = s;
        this.result = result;
    }
//    private String error;
//    private String warnings;
//    private String path;
//    private Object data;
//
//    {
//        "timestamp": "2023-05-20T09:09:32.723+00:00",
//            "path": "/users/friends",
//            "status": 500,
//            "error": "Internal Server Error",
//            "requestId": "ac9514f0-2"
//    }
}
