package com.example.foodserviceapp.advice;


import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

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
    public Object verifyByRequestMember(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestMemberEmail = "";

        log.info("# My JWT Advice Apply");
        requestMemberEmail = Arrays.stream(joinPoint.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst().orElseThrow(() -> new ServiceLogicException(ErrorCode.BAD_REQUEST));

        Object proceed = joinPoint.proceed();
        verifyByUserNameAndEmail(requestMemberEmail, proceed);


        return proceed;
    }

    /* TODO 현재 사용되고 있는곳 없음 추후 삭제 할수도 있음*/
    @Around("@annotation(JwtPagePoint)")
    @Transactional
    public Object verifyByRequestMemberForPage(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestMemberEmail = "";
        log.info("# My JWT Advice Apply For Page");
        requestMemberEmail = Arrays.stream(joinPoint.getArgs())
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .findFirst().orElseThrow(() -> new ServiceLogicException(ErrorCode.BAD_REQUEST));

        Object proceed = joinPoint.proceed();

        List content = ((Page) proceed).getContent();
        for (Object obj : content) {
            verifyByUserNameAndEmail(requestMemberEmail,obj);
        }

        return proceed;

    }

    public void verifyByUserNameAndEmail(String requestMemberEmail, Object obj) {
        String findMemberEmail = "";
        if (obj instanceof Order) {
            findMemberEmail = ((Order) obj).getMember().getEmail();
        } else if (obj instanceof Member) {
            findMemberEmail =((Member) obj).getEmail();
        }

        if(!requestMemberEmail.equals(findMemberEmail) && !requestMemberEmail.equals(adminMailAddress))
            throw new ServiceLogicException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
}
