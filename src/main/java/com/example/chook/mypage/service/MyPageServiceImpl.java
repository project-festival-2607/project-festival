package com.example.chook.mypage.service;

import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.mypage.DTO.MyPageDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MyPageServiceImpl implements MyPageService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 마이페이지 조회
    @Override
    public MyPageDTO getMyPage(String username) {

        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow();

        return memberEntityToDTO(member);
    }

    // 개인정보 수정
    @Transactional
    @Override
    public void modify(String username, MyPageDTO myPageDTO) {
        // 현재 로그인한 회원 조회
        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow();

        log.info("수정 전 Member = {}", member);

        // 이름 수정
        member.setName(myPageDTO.getName());

        // 전화번호 수정
        member.setPhone(myPageDTO.getPhone());

        // 이메일 수정
        member.setEmail(myPageDTO.getEmail());

        // 비밀번호 수정
        if (myPageDTO.getPassword() != null
                && !myPageDTO.getPassword().isBlank()){
            member.setPasswordHash(passwordEncoder.encode(myPageDTO.getPassword()));

            log.info("수정 후 Member = {}", member);
        }
    }
}
