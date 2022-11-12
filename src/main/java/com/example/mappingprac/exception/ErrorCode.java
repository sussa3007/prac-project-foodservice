package com.example.mappingprac.exception;

import lombok.Getter;

public enum ErrorCode {

    MEMBER_NOT_FOUND(404, "MEMBER NOT FOUND"),
    MEMBER_EXISTS(409, "MEMBER EXISTS"),
    FOOD_NOT_FOUND(404, "FOOD NOT FOUND"),
    FOOD_CODE_EXISTS(409, "FOOD CODE EXISTS"),
    FOOD_EXISTS(409, "FOOD EXISTS"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),
    NOT_IMPLEMENTED(501,"NOT IMPLEMENTED");

    
    @Getter
    private int status;

    @Getter
    private String message;

    ErrorCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
