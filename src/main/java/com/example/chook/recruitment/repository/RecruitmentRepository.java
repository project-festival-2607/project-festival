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
  // publishedAt은 최초 결제 시점에만 채워지고 이후로는 절대 바뀌지 않아야 하므로 COALESCE로 보호
  @Modifying
  @Query("UPDATE Recruitment r SET r.publishedAt = COALESCE(r.publishedAt, CURRENT_TIMESTAMP), r.status = :status WHERE r.id = :id")
  void publish(@Param("id") Long id, @Param("status") RecruitmentStatus status);

  @Modifying
  @Query("UPDATE Recruitment r SET r.status = :status WHERE r.id = :id")
  void updateStatus(@Param("id") Long id, @Param("status") RecruitmentStatus status);

  // 모집 날짜가 지난 게시글(RECRUITING/PAUSED)만 자동으로 마감 처리 - DRAFT는 아직 공개된 적 없으므로 대상에서 제외
  @Modifying
  @Query("UPDATE Recruitment r SET r.status = com.example.chook.recruitment.entity.enums.RecruitmentStatus.CLOSED " +
    "WHERE r.applicationDeadline < CURRENT_DATE " +
    "AND r.status IN (com.example.chook.recruitment.entity.enums.RecruitmentStatus.RECRUITING, com.example.chook.recruitment.entity.enums.RecruitmentStatus.PAUSED)")
  int closeExpiredRecruitments();

}
