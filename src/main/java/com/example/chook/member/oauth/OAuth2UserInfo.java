package com.example.chook.member.oauth;

import com.example.chook.member.entity.enums.Provider;

// provider 응답을 처리하는 공통 규격 인터페이스
public interface OAuth2UserInfo {

    // 구글, 네이버, 카카오
    Provider getProvider();

    // 연동 식별 고유 id
    String getProviderId();

    // 이메일
    String getEmail();

    // 프로필 상의 이름
    String getName();

}
