package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ResumeFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {

    // 포트폴리오 첨부파일 조회
    Optional<ResumeFile> findByResumePortfolio_Id(Long portfolioId);

    // 포트폴리오 첨부파일 삭제 (deleteAllByResumePortfolio_Resume_Id -> 조건에 맞는 모든 데이터를 삭제하는 메서드)
    void deleteAllByResumePortfolio_Resume_Id(Long portfolioId);

    // 특정 포트폴리오의 첨부파일 삭제
    void deleteAllByResumePortfolio_Id(Long portfolioId);
}
