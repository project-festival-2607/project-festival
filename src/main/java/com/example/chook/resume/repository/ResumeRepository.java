package com.example.chook.resume.repository;

import com.example.chook.resume.dto.ResumeResponseDTO;
import com.example.chook.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    // applypage Zone
    ResumeResponseDTO getResumeByMemberId(Long memberId);
}
