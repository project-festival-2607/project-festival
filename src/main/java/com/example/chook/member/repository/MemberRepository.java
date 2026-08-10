package com.example.chook.member.repository;

import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 로그인
    Optional<Member> findByUsernameAndDeletedAtIsNull(String username);

    // 회원가입 아이디 중복 체크
    boolean existsByUsername(String username);

    // 아이디 찾기 - 탈퇴 계정은 검색 결과에서 제외
    List<Member> findByPhoneAndDeletedAtIsNull(String phone);
    List<Member> findByEmailAndDeletedAtIsNull(String email);

    // 비밀번호 찾기 (아이디 + 휴대폰/이메일 본인확인)
    Optional<Member> findByUsernameAndPhoneAndDeletedAtIsNull(String username, String phone);
    Optional<Member> findByUsernameAndEmailAndDeletedAtIsNull(String username, String email);

    // 소셜 로그인 이메일 중복 체크
    boolean existsByEmailAndDeletedAtIsNullAndRoleIn(String email, List<MemberRole> roles);

    // member 강제 삭제용 메서드
    void deleteByUsername(String username);

    // 역할 별 member 조회
    List<Member> findByRole(MemberRole role);
}
