package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>,
  RecruitmentRepositoryCustom {

  long countByFestival_ContentId(String contentId);

  List<Recruitment> findByFestival_ContentId(String contentId);

  // CURRENT_TIMESTAMP: 애플리케이션이 아니라 DB가 UPDATE를 실행하는 시점의 시간을 넣음
  // status도 함께 바꿔야 목록 조회(RECRUITING만 노출)에 나타남
  @Modifying
  @Query("UPDATE Recruitment r SET r.publishedAt = CURRENT_TIMESTAMP, r.status = :status WHERE r.id = :id")
  void publish(@Param("id") Long id, @Param("status") RecruitmentStatus status);

}
