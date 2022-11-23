package com.example.foodserviceapp.member.controller;

import com.example.foodserviceapp.dto.PageResponseDto;
import com.example.foodserviceapp.dto.ResponseDto;
import com.example.foodserviceapp.member.dto.MemberDto;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Validated
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity postMember(
            @Valid @RequestBody MemberDto.Post request
            ) {
        Member member = request.postDtoToMember();
        Member createdMember = memberService.createMember(member);
        MemberDto.Response response = MemberDto.Response.memberToResponseDto(createdMember);
        return new ResponseEntity(ResponseDto.of(response), HttpStatus.CREATED);
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(
            @PathVariable("member-id") @Positive Long memberId,
            @RequestBody @Valid MemberDto.Patch request
    ) {
        request.setMemberId(memberId);
        Member member = request.patchDtoToMember();
        Member updateMember = memberService.updateMember(member);
        MemberDto.Response response = MemberDto.Response.memberToResponseDto(updateMember);
        return new ResponseEntity<>(ResponseDto.of(response), HttpStatus.OK);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(
            @PathVariable("member-id") @Positive Long memberId
    ) {
        Member findMember = memberService.findMember(memberId);
        MemberDto.Response response = MemberDto.Response.memberToResponseDto(findMember);
        return new ResponseEntity<>(ResponseDto.of(response), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMembers(
            @RequestParam @Positive int page,
            @RequestParam @Positive int size
    ) {
        Page<Member> memberPage = memberService.findMembers(page-1,size);
        List<MemberDto.Response> responseList =
                MemberDto.Response.memberListToResponseDtos(memberPage.getContent());
        return new ResponseEntity<>(PageResponseDto.of(responseList, memberPage), HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(
            @PathVariable("member-id") @Positive Long memberId
    ) {
        memberService.deleteMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /* Test member 생성*/
    @GetMapping("/test")
    public ResponseEntity testPost() {
        MemberDto.Post test1 = new MemberDto.Post("test1", "test1@gmail.com", "1111!","010-1111-1111");
        MemberDto.Post test2 = new MemberDto.Post("test2", "test2@gmail.com", "1111!","010-1111-1112");
        MemberDto.Post test3 = new MemberDto.Post("test3", "test3@gmail.com", "1111!","010-1111-1113");
        MemberDto.Post admin = new MemberDto.Post("admin", "admin@gmail.com", "1111!","010-1111-1119");
        memberService.createMember(test1.postDtoToMember());
        memberService.createMember(test2.postDtoToMember());
        memberService.createMember(test3.postDtoToMember());
        memberService.createMember(admin.postDtoToMember());
        return new ResponseEntity<>("Good",HttpStatus.OK);
    }

}
