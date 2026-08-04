package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ResumeCareer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeCareerRepository extends JpaRepository<ResumeCareer, Long> {
}
