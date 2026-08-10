package com.example.chook.mypage;

import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MyPageServiceImpl implements MyPageService{

    private final MemberRepository memberRepository;

    @Override
    public MyPageDTO getMyPage(String username) {

        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow();

        return memberEntityToDTO(member);
    }
}
