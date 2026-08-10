package com.example.chook.member.security;

import com.example.chook.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole().getRole()));
    }

    @Override
    public String getPassword() {
        return member.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // isAccountNonExpired / isAccountNonLocked / isCredentialsNonExpired / isEnabled는
    // 인터페이스 기본값(true)을 그대로 사용한다.
    // Spring Security는 이 값들을 비밀번호 검증 "전"에 확인하기 때문에,
    // 여기서 DORMANT/SUSPENDED를 반영하면 비밀번호 확인 없이 계정 상태가 노출된다.
    // 그래서 상태 판단은 LoginSuccessHandler(인증 성공 후)에서 처리한다.

}