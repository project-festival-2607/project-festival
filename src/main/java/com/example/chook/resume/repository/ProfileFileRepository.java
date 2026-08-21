package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ProfileFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileFileRepository extends JpaRepository<ProfileFile, Long> {

    // 프로필파일 조회
    Optional<ProfileFile> findByResume_Id(Long resumeId);

    // 프로필파일 삭제
    void deleteByResume_Id(Long resumeId);
}
