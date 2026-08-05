package com.example.chook.member.service;

import com.example.chook.member.dto.LoginRequestDTO;
import com.example.chook.member.dto.LoginResponseDTO;
import com.example.chook.member.entity.Member;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder  passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {

        Member member = memberRepository.findByUsernameAndDeletedAtIsNull(requestDTO.getUsername())
                .filter(m -> passwordEncoder.matches(requestDTO.getPassword(), m.getPasswordHash()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        switch (member.getStatus()) {
            case DORMANT -> throw new MemberDormantException();
            case SUSPENDED ->  throw new MemberSuspendedException();
            default -> {}
        }

        return LoginResponseDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }

}
