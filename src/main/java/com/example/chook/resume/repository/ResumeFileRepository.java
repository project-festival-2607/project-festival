package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ResumeFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {
}
