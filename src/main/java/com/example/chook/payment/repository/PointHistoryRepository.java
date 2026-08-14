package com.example.chook.payment.repository;

import com.example.chook.payment.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Integer> {

  // 모집공고 삭제 시 감사 로그(PointHistory)는 남기되 참조만 끊음 (recruit_id nullable)
  @Modifying
  @Query("UPDATE PointHistory ph SET ph.recruit = null WHERE ph.recruit.id = :recruitId")
  void clearRecruitReference(@Param("recruitId") Long recruitId);

}