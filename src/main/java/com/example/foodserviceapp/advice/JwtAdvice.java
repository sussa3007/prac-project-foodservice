package com.example.foodserviceapp.advice;


import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class JwtAdvice {

    private final String adminMailAddress;

    public JwtAdvice(@Value("${mail.address.admin}") String adminMailAddress) {
        this.adminMailAddress = adminMailAddress;
    }

    @Around("@annotation(JwtPoint)")
    @Transactional
    public Object verifyOrderByRequestMember(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestMemberEmail = "";
        String findMemberEmail = "";
        log.info("# My JWT Advice Apply");
        Object proceed = joinPoint.proceed();
        requestMemberEmail = Arrays.stream(joinPoint.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst().orElseThrow(() -> new ServiceLogicException(ErrorCode.INTERNAL_SERVER_ERROR));
        if (proceed instanceof Order) {
            findMemberEmail = ((Order) proceed).getMember().getEmail();
            verifyByUserNameAndEmail(requestMemberEmail, findMemberEmail);
        }
        return proceed;
    }
    public void verifyByUserNameAndEmail(String requestMemberEmail, String findMemberEmail) {
        if(!requestMemberEmail.equals(findMemberEmail) && !requestMemberEmail.equals(adminMailAddress))
            throw new ServiceLogicException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
}
