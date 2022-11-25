package com.example.foodserviceapp.advice;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS) // Retention(유지 범위) 기본값은 CLASS
@Documented // 자바 도큐먼트 작성
@Target(ElementType.METHOD) // 메소드 위에 어노테이션을 설정
public @interface JwtPagePoint {
}
