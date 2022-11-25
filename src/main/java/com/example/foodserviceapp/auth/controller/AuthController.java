package com.example.foodserviceapp.auth.controller;

import com.example.foodserviceapp.auth.JwtTokenizer;
import com.example.foodserviceapp.dto.LoginSuccessResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final JwtTokenizer jwtTokenizer;

    @GetMapping("/reissuetoken")
    public ResponseEntity reIssueToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        jwtTokenizer.verifyRefreshToken(request.getHeader("Refresh"),response);


        return new ResponseEntity<>(LoginSuccessResponseDto.of(response), HttpStatus.OK);
    }

}
