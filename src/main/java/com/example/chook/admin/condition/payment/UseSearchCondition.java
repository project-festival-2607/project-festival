package com.example.chook.admin.condition.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.use.UseDateRangeType;
import com.example.chook.admin.enums.payment.use.UseKeywordType;
import com.example.chook.admin.enums.payment.use.UseSortCriteria;
import com.example.chook.admin.form.payment.UseSearchForm;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record UseSearchCondition(

  // 공통 검색 폼
  UseKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  UseDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 정렬
  UseSortCriteria sortCriteria,
  Boolean ascending

) {

  public static UseSearchCondition from(UseSearchForm form) {

    UseKeywordType keywordType = toEnum(form.keywordType(), UseKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return UseSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), UseDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .sortCriteria(toEnum(form.sortCriteria(), UseSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
