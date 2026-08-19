package com.example.chook.admin.condition.board;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.notice.NoticeDateRangeType;
import com.example.chook.admin.entity.enums.notice.NoticeKeywordType;
import com.example.chook.admin.entity.enums.notice.NoticeSortCriteria;
import com.example.chook.admin.form.board.NoticeSearchForm;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record NoticeSearchCondition(

  // 공통 검색 폼
  NoticeKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  NoticeDateRangeType dateRangeType,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터
  Boolean highlight,

  // 테이블 상단 정렬
  NoticeSortCriteria sortCriteria,
  Boolean ascending

) {

  public static NoticeSearchCondition from(NoticeSearchForm form) {

    NoticeKeywordType keywordType = toEnum(form.keywordType(), NoticeKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    Boolean highlight = Objects.equals(form.highlight(), "true") ? Boolean.TRUE :
      (Objects.equals(form.highlight(), "false") ? Boolean.FALSE : null);

    return NoticeSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), NoticeDateRangeType.class, null))
      .startDate(form.startDate())
      .endDate(form.endDate())
      .highlight(highlight)
      .sortCriteria(toEnum(form.sortCriteria(), NoticeSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
