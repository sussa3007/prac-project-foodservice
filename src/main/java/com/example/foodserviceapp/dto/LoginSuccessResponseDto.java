package com.example.foodserviceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessResponseDto {

    private String accessToken;
    private String refreshToken;

    public static LoginSuccessResponseDto of(HttpServletResponse response) {
        return new LoginSuccessResponseDto(response.getHeader("Authorization"),response.getHeader("Refresh"));
    }
}
