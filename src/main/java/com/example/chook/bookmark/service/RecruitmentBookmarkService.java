package com.example.chook.bookmark.service;

import java.util.Set;

public interface RecruitmentBookmarkService {

  // 이미 찜한 상태면 해제하고 false, 아니면 찜하고 true를 반환
  boolean toggle(Long recruitmentId, Long memberId);

  boolean isBookmarked(Long recruitmentId, Long memberId);

  Set<Long> getBookmarkedRecruitmentIds(Long memberId);

}
