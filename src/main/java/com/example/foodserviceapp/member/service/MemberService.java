package com.example.foodserviceapp.member.service;

import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Positive;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Member createMember(Member member) {
        String email = member.getEmail();
        verifyMemberByEmail(email);
        return memberRepository.save(member);
    }

    public Member updateMember(Member member) {
        return checkMemberField(member);
    }
    public Member findMember(Long memberId) {
        return verifiedMemberById(memberId);
    }

    public Page<Member> findMembers(int page, int size) {
        return memberRepository.findAllByOrderByMemberIdDesc(PageRequest.of(page, size));
    }

    public void deleteMember(Long memberId) {
        Member findMember = verifiedMemberById(memberId);
        findMember.setStatus(Member.MemberStatus.WITHDRAWAL_MEMBER);
    }


    /* 검증 로직 */
    private Member checkMemberField(Member member) {
        Member findMember = verifiedMemberById(member.getMemberId());
        Optional.ofNullable(member.getName())
                .ifPresent(findMember::setName);
        Optional.ofNullable(member.getEmail())
                .ifPresent(findMember::setEmail);
        Optional.ofNullable(member.getPhone())
                .ifPresent(findMember::setPhone);
        return findMember;
    }

    private void verifyMemberByEmail(String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isPresent())
            throw new ServiceLogicException(ErrorCode.MEMBER_EXISTS);
    }

    private Member verifiedMemberById(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        return findMember.orElseThrow(
                () -> new ServiceLogicException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }


}
