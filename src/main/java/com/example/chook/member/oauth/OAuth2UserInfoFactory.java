package com.example.chook.member.oauth;

import java.util.Map;

// Spring Security가 로그인 콜백 처리 중 넘겨주는 registrationId(google, naver, kakao)를 확인
// 3가지 UserInfo 중 어떤 클래스로 인스턴스를 생성할지 결정
public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인 provider입니다: " + registrationId);
        };
    }

}