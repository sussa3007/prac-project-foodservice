package com.example.foodserviceapp.auth.utils;

import lombok.*;

@ToString
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class Token {
    private String accessToken;
    private String refreshToken;
}