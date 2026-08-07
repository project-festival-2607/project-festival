package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ResumePortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumePortfolioRepository extends JpaRepository<ResumePortfolio, Long> {

    // 포트폴리오 조회
    List<ResumePortfolio> findByResume_Id(Long resumeId);

    // 포트폴리오 삭제
    void deleteAllByResume_Id(Long resumeId);
}
