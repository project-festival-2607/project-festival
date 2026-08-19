package com.example.chook.admin.condition.event;

import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.recruitment.RecruitmentDateRangeType;
import com.example.chook.admin.entity.enums.recruitment.RecruitmentKeywordType;
import com.example.chook.admin.entity.enums.recruitment.RecruitmentSortCriteria;
import com.example.chook.admin.form.event.RecruitmentSearchForm;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record RecruitmentSearchCondition(

  // 공통 검색 폼
  RecruitmentKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  RecruitmentDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,
  LocalDate startDate,
  LocalDate endDate,

  // 테이블 상단 필터
  RecruitmentCategory category,
  String sidoCode,
  String sigunguCode,
  RecruitmentStatus status,

  // 테이블 상단 정렬
  RecruitmentSortCriteria sortCriteria,
  Boolean ascending

) {

  public static RecruitmentSearchCondition from(RecruitmentSearchForm form) {

    RecruitmentKeywordType keywordType = toEnum(form.keywordType(), RecruitmentKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return RecruitmentSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), RecruitmentDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .startDate(form.startDate())
      .endDate(form.endDate())
      .category(toEnum(form.category(), RecruitmentCategory.class, null))
      .sidoCode(form.sidoCode())
      .sigunguCode(form.sigunguCode())
      .status(toEnum(form.status(), RecruitmentStatus.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), RecruitmentSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }
}
