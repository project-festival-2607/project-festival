package com.example.chook.application.repository;

import com.example.chook.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 내가 지원한 목록 조회
    List<Application> findByMemberId(Long memberId);

    // 특정 모집공고에 지원한 구직자 목록 조회
    List<Application> findByRecruitmentId(Long recruitmentId);

    // 같은 공고에 이미 지원했는지 확인 (applypage Zone)
    boolean existsByMemberIdAndRecruitmentId(Long memberId, Long recruitmentId);

    // 지원 이력 존재 여부 확인(applypage Zone)
    boolean existsByResumeId(Long resumeId);
}
