package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.RecruitmentFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentFileRepository extends JpaRepository<RecruitmentFile, Long> {

  List<RecruitmentFile> findByRecruitment_Id(Long recruitmentId);

}
