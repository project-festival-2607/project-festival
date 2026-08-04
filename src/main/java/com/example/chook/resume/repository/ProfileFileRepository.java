package com.example.chook.resume.repository;

import com.example.chook.resume.entity.ProfileFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileFileRepository extends JpaRepository<ProfileFile, Long> {
}
