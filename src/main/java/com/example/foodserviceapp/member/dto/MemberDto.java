package com.example.foodserviceapp.member.dto;

import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.entity.Point;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MemberDto {

    @Data
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String name;

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}",message = "휴대폰 번호를 올바르게 입력해 주세요")
        private String phone;

        public Member postDtoToMember() {
            return Member.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(this.password)
                    .phone(this.phone)
                    .point(new Point())
                    .status(Member.MemberStatus.ACTIVE_MEMBER)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class Patch {

        private Long memberId;

        private String name;

        @Email
        private String email;

        private String password;

        @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}",message = "휴대폰 번호를 올바르게 입력해 주세요")
        private String phone;

        public Member patchDtoToMember() {
            return Member.builder()
                    .memberId(this.memberId)
                    .name(this.name)
                    .email(this.email)
                    .password(this.password)
                    .phone(this.phone)
                    .build();
        }
    }
    @Data
    @AllArgsConstructor
    public static class Response {
        private Long memberId;

        private String name;

        private String email;

        private String phone;

        private Member.MemberStatus status;

        private Point point;

        public String getStatus() {
            return this.status.getStatus();
        }

        public int getPoint() {
            return this.point.getPointCount();
        }

        public static Response memberToResponseDto(Member member) {
            return new MemberDto.Response(
                    member.getMemberId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone(),
                    member.getStatus(),
                    member.getPoint()
            );
        }

        public static List<Response> memberListToResponseDtos(List<Member> members) {
            return members.stream()
                    .filter(member -> member.getStatus() == Member.MemberStatus.ACTIVE_MEMBER)
                    .map(Response::memberToResponseDto)
                    .collect(Collectors.toList());
        }
    }



}
