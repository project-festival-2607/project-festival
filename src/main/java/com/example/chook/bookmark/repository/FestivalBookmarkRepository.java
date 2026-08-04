package com.example.chook.bookmark.repository;

import com.example.chook.bookmark.entity.FestivalBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FestivalBookmarkRepository extends JpaRepository<FestivalBookmark, Long> {

  List<FestivalBookmark> findAllByFestival_ContentId(String contentId);
  List<FestivalBookmark> findAllByMember_Id(Long memberId);

  boolean existsByFestival_ContentIdAndMember_Id(
    String contentId,
    Long memberId
  );

}
