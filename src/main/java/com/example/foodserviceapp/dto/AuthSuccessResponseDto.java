package com.example.foodserviceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthSuccessResponseDto {

    private String accessToken;
    private String refreshToken;

    public static AuthSuccessResponseDto of(HttpServletResponse response) {
        return new AuthSuccessResponseDto(response.getHeader("Authorization"),response.getHeader("Refresh"));
    }
}
