package com.example.chook.resume.repository;

import com.example.chook.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // 이력서 관리 페이지로 이동
    Optional<Resume> findByMemberId(Long memberId);
}
