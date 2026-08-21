package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ResumeCareer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeCareerRepository extends JpaRepository<ResumeCareer, Long> {

    // 경력 조회
    List<ResumeCareer> findByResume_Id(Long resumeId);

    // 경력 삭제
    void deleteAllByResume_Id(Long resumeId);
}
