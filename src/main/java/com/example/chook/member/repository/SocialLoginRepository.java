package com.example.chook.member.repository;

import com.example.chook.member.entity.SocialLogin;
import com.example.chook.member.entity.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialLoginRepository extends JpaRepository<SocialLogin,Long> {

    // 소셜 로그인 시 연동 계정 조회
    Optional<SocialLogin> findByProviderAndProviderId(Provider provider, String providerId);
    boolean existsByProviderAndProviderId(Provider provider, String providerId);

    // 마이페이지 - 연동 계정 목록/중복 연동 체크
    List<SocialLogin> findByMemberId(Long memberId);
    boolean existsByMemberIdAndProvider(Long memberId, Provider provider);

    // 개별 연동 해제
    void deleteByMemberIdAndProvider(Long memberId, Provider provider);

    // 회원 탈퇴 시 연동된 소셜 계정 전체 삭제
    void deleteByMemberId(Long memberId);
}
