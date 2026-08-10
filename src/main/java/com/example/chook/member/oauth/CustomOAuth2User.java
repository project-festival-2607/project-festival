package com.example.chook.member.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// Spring Security가 OAuth2 로그인 사용자를 표현할 때 사용하는 OAuth2User 구현 클래스
public class CustomOAuth2User implements OAuth2User {

    // 구글, 네이버, 카카오가 전달한 원본 정보
    private final Map<String, Object> attributes;

    // provider별로 다른 JSON 정보를 Info 공통 형태로 변환한 객체
    private final OAuth2UserInfo userInfo;

    // 원본 정보와 공통 형태를 인자로 받아 객체 생성
    public CustomOAuth2User(Map<String, Object> attributes, OAuth2UserInfo userInfo) {
        this.attributes = attributes;
        this.userInfo = userInfo;
    }

    // Service에서 변환된 정보를 사용할 수 있도록 반환
    public OAuth2UserInfo getUserInfo() {
        return userInfo;
    }

    // Spring Security가 OAuth2 제공자로부터 받은 원본 정보 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 멤버 권한 반환
    // ROLE_SOCIAL은 ENUM에 정의된 DB값이 아님
    // Spring Security의 OAuth2User 인터페이스 규격에 맞추기 위한 임시값
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_SOCIAL"));
    }

    // 회원 식별자 반환
    @Override
    public String getName() {
        return userInfo.getProviderId();
    }

}