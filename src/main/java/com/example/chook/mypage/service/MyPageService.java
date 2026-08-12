package com.example.chook.mypage.service;

import com.example.chook.member.entity.Member;
import com.example.chook.mypage.DTO.MyPageDTO;

public interface MyPageService {

    // member (Entity) → MyPageDTO 변환
    default MyPageDTO memberEntityToDTO(
            Member member
    ) {
        return MyPageDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .phone(member.getPhone())
                .email(member.getEmail())
                .point(member.getPoint())
                .role(member.getRole())
                .build();
    }

    // 마이페이지 조회
    MyPageDTO getMyPage(String username);

    void modify(String username, MyPageDTO myPageDTO);
}
