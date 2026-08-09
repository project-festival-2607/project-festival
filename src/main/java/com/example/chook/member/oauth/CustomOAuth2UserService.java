package com.example.chook.member.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@Slf4j
// OAuth2 로그인 과정에서 회원 정보를 가져오는 Service
// DefaultOAuth2UserService를 상속, 기본 정보 조회 기능을 그대로 사용
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 구글, 네이버, 카카오 등 OAuth2 제공자로부터 회원 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 현재 로그인에 사용된 제공자 확인(구글, 네이버, 카카오 중 하나)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 제공자마다 다른 JSON 형태를 공통 OAuth2UserInfo 형태로 변환
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        // 로그인 시도 로그 출력
        log.info("OAuth2 로그인 시도: provider={}, providerId={}", userInfo.getProvider(), userInfo.getProviderId());

        // 원본 OAuth2 정보와 변환된 Info 형태 정보를 하나의 객체로 반환
        return new CustomOAuth2User(oAuth2User.getAttributes(), userInfo);
    }

}