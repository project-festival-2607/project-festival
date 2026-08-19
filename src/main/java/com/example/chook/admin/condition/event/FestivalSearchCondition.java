package com.example.chook.admin.condition.event;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.festival.FestivalDateRangeType;
import com.example.chook.admin.entity.enums.festival.FestivalKeywordType;
import com.example.chook.admin.entity.enums.festival.FestivalSortCriteria;
import com.example.chook.admin.form.event.FestivalSearchForm;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record FestivalSearchCondition(

  // 공통 검색 폼
  FestivalKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  FestivalDateRangeType dateRangeType,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터

  // 테이블 상단 정렬
  FestivalSortCriteria sortCriteria,
  Boolean ascending

) {

  public static FestivalSearchCondition from(FestivalSearchForm form) {

    FestivalKeywordType keywordType = toEnum(form.keywordType(), FestivalKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return FestivalSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), FestivalDateRangeType.class, null))
      .startDate(form.startDate())
      .endDate(form.endDate())
      .sortCriteria(toEnum(form.sortCriteria(), FestivalSortCriteria.class, null))
      .ascending(form.ascending())
      .build();
  }
}
