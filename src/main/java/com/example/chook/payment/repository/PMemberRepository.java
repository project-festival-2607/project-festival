package com.example.chook.payment.repository;

import com.example.chook.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PMemberRepository extends JpaRepository<Member, Long> {

    // 결제 자격이 있는 회원: RECRUITER이면서, 본인이 개최한 축제에
    // 모집공고(recruitment)가 하나 이상 등록되어 있는 사람
    @Query(value = """
        SELECT DISTINCT m.id AS id, m.username AS username, m.name AS name
        FROM members m
        JOIN festival f ON  f.member_id = m.id
        JOIN recruitment r ON r.festival_id = f.content_id
        WHERE m.role = 'RECRUITER'
        ORDER BY m.id
        """, nativeQuery = true)
    List<EligibleMemberView> findEligiblePayers();

    // COUNT(*) > 0을 DB에서 boolean으로 바로 받으면 Hibernate 네이티브 쿼리에서
    // 타입 매핑 에러가 나서, 숫자(Long)로 받아 자바에서 비교하는 방식으로 처리
    @Query(value = """
        SELECT COUNT(*)
        FROM members m
        JOIN festival  f.member_id = m.id
        JOIN recruitment r ON r.festival_id = f.content_id
        WHERE m.role = 'RECRUITER' AND m.id = :memberId
        """, nativeQuery = true)
    Long checkEligiblePayer(@Param("memberId") Long memberId);

    default boolean isEligiblePayer(Long memberId) {
        Long count = checkEligiblePayer(memberId);
        return count != null && count > 0;
    }

    // 테스트 계정 빠른 로그인용
    Optional<Member> findByUsername(String username);
}