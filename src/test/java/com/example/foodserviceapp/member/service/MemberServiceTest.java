package com.example.foodserviceapp.member.service;

import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.entity.Point;
import com.example.foodserviceapp.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("이미 등록된 회원 생성시 예외 발생 - 이메일 조회")
    void createMemberException() {
        // Given
        Long memberId = 1L;
        Member testMember = createTestMember(memberId);
        // When
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(testMember));
        Throwable throwable = catchThrowable(() -> memberService.createMember(Member.builder()
                .email("PreventNull").build()));
        // Then
        assertThat(throwable)
                .isInstanceOf(ServiceLogicException.class)
                .hasMessageContaining(ErrorCode.MEMBER_EXISTS.getMessage());
    }
    @Test
    @DisplayName("등록 되지 않은 회원 접근 예외 발생- 아이디 조회")
    void verifiedMemberByIdException() {
        // Given
        Long memberId = 1L;
        // When
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(() -> memberService.findMember(memberId));
        // Then
        assertThat(throwable)
                .isInstanceOf(ServiceLogicException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    private Member createTestMember(Long memberId) {
        return Member.builder()
                .memberId(memberId)
                .name("Test")
                .email("test@gmail.com")
                .phone("010-1111-1111")
                .status(Member.MemberStatus.ACTIVE_MEMBER)
                .point(new Point())
                .build();
    }

}