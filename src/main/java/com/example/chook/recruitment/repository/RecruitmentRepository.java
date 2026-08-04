package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

  long countByFestival_ContentId(String contentId);
  List<Recruitment> findByFestival_ContentId(String contentId);

}
