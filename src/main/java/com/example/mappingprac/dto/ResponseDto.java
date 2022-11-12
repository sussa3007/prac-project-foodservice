package com.example.mappingprac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class ResponseDto {

    private Object data;

    public static ResponseDto of(Object data) {
        return new ResponseDto(data);
    }
}
