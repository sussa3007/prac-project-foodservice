package com.example.foodserviceapp.member.service;

import com.example.foodserviceapp.advice.JwtPoint;
import com.example.foodserviceapp.auth.utils.JwtAuthorityUtils;
import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final String adminEmail;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtAuthorityUtils authorityUtils;

    public MemberService(
            @Value("${mail.address.admin}") String adminEmail,
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtAuthorityUtils authorityUtils
    ) {
        this.adminEmail = adminEmail;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    public Member createMember(Member member) {
        String email = member.getEmail();
        verifyMemberByEmail(email);

        String encodePassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodePassword);

        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        return memberRepository.save(member);
    }


    @JwtPoint
    public Member updateMember(Member member, String email) {
        return checkMemberField(member);
    }

    @Transactional(readOnly = true)
    @JwtPoint
    public Member findMember(Long memberId,String email) {
        return verifiedMemberById(memberId);
    }

    @Transactional(readOnly = true)
    public Page<Member> findMembers(String email,int page, int size) {
        if (email.equals(adminEmail)) {
            return memberRepository.findAllByOrderByMemberIdDesc(PageRequest.of(page, size));
        }
        throw new ServiceLogicException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    @JwtPoint
    public Member deleteMember(Long memberId, String email) {
        Member findMember = verifiedMemberById(memberId);
        findMember.setStatus(Member.MemberStatus.WITHDRAWAL_MEMBER);
        return findMember;
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
        Optional.ofNullable(member.getPassword())
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
