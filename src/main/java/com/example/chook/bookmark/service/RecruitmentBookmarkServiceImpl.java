package com.example.chook.bookmark.service;

import com.example.chook.bookmark.entity.RecruitmentBookmark;
import com.example.chook.bookmark.repository.RecruitmentBookmarkRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentBookmarkServiceImpl implements RecruitmentBookmarkService {

  private final RecruitmentBookmarkRepository recruitmentBookmarkRepository;
  private final EntityManager entityManager;

  @Override
  @Transactional
  public boolean toggle(Long recruitmentId, Long memberId) {
    return recruitmentBookmarkRepository.findByRecruitment_IdAndMember_Id(recruitmentId, memberId)
      .map(existing -> {
        recruitmentBookmarkRepository.delete(existing);
        return false;
      })
      .orElseGet(() -> {
        recruitmentBookmarkRepository.save(
          RecruitmentBookmark.builder()
            .recruitment(entityManager.getReference(Recruitment.class, recruitmentId))
            .member(entityManager.getReference(Member.class, memberId))
            .build()
        );
        return true;
      });
  }

  @Override
  public boolean isBookmarked(Long recruitmentId, Long memberId) {
    return recruitmentBookmarkRepository.existsByRecruitment_IdAndMember_Id(recruitmentId, memberId);
  }

  @Override
  public Set<Long> getBookmarkedRecruitmentIds(Long memberId) {
    return Set.copyOf(recruitmentBookmarkRepository.findRecruitmentIdsByMemberId(memberId));
  }

}