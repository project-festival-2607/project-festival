package com.example.chook.admin.condition.payment;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.payment.product.ProductDateRangeType;
import com.example.chook.admin.enums.payment.product.ProductKeywordType;
import com.example.chook.admin.enums.payment.product.ProductSortCriteria;
import com.example.chook.admin.enums.payment.product.ProductStatus;
import com.example.chook.admin.form.payment.ProductSearchForm;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record ProductSearchCondition(

  // 공통 검색 폼
  ProductKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  ProductDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  ProductStatus status,

  // 테이블 상단 정렬
  ProductSortCriteria sortCriteria,
  Boolean ascending

) {

  public static ProductSearchCondition from(ProductSearchForm form) {

    ProductKeywordType keywordType = toEnum(form.keywordType(), ProductKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return ProductSearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), ProductDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .status(toEnum(form.status(), ProductStatus.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), ProductSortCriteria.class, null))
      .ascending(form.ascending())
      .build();

  }

}
