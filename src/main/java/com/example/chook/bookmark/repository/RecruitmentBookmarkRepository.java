package com.example.chook.bookmark.repository;

import com.example.chook.bookmark.entity.RecruitmentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitmentBookmarkRepository extends JpaRepository<RecruitmentBookmark, Long> {

  List<RecruitmentBookmark> findAllByRecruitment_Id(Long recruitmentId);
  List<RecruitmentBookmark> findAllByMember_Id(Long memberId);

  boolean existsByRecruitment_IdAndMember_Id(
    Long recruitmentId,
    Long memberId
  );

  Optional<RecruitmentBookmark> findByRecruitment_IdAndMember_Id(
    Long recruitmentId,
    Long memberId
  );

  @Query("select rb.recruitment.id from RecruitmentBookmark rb where rb.member.id = :memberId")
  List<Long> findRecruitmentIdsByMemberId(@Param("memberId") Long memberId);

}
