package com.example.chook.bookmark.repository;

import com.example.chook.bookmark.entity.RecruitmentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentBookmarkRepository extends JpaRepository<RecruitmentBookmark, Long> {

  List<RecruitmentBookmark> findAllByRecruitment_Id(Long recruitmentId);
  List<RecruitmentBookmark> findAllByMember_Id(Long memberId);

  boolean existsByRecruitment_IdAndMember_Id(
    Long recruitmentId,
    Long memberId
  );

}
