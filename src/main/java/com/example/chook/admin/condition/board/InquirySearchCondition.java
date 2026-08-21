package com.example.chook.admin.condition.board;

import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.board.inquiry.InquiryDateRangeType;
import com.example.chook.admin.enums.board.inquiry.InquiryKeywordType;
import com.example.chook.admin.enums.board.inquiry.InquiryReplyStatus;
import com.example.chook.admin.enums.board.inquiry.InquirySortCriteria;
import com.example.chook.admin.form.board.InquirySearchForm;
import com.example.chook.member.entity.enums.MemberRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCriteria;
import static com.example.chook.admin.util.KeywordUtils.getKeywordList;
import static com.example.chook.common.util.CustomStringUtils.toEnum;

@Builder
public record InquirySearchCondition(

  // 공통 검색 폼
  InquiryKeywordType keywordType,
  List<String> keywordList,
  KeywordCriteria keywordCriteria,
  InquiryDateRangeType dateRangeType,
  LocalDateTime startDateTime,
  LocalDateTime endDateTime,

  // 테이블 상단 필터
  InquiryReplyStatus replyStatus,
  MemberRole memberRole,

  // 테이블 상단 정렬
  InquirySortCriteria sortCriteria,
  Boolean ascending

) {

  public static InquirySearchCondition from(InquirySearchForm form) {

    InquiryKeywordType keywordType = toEnum(form.keywordType(), InquiryKeywordType.class, null);
    KeywordCriteria keywordCriteria = getKeywordCriteria(keywordType, form.keywordCriteria());
    List<String> keywordList = getKeywordList(form.keywords(), keywordCriteria);

    return InquirySearchCondition.builder()
      .keywordType(keywordType)
      .keywordList(keywordList)
      .keywordCriteria(keywordCriteria)
      .dateRangeType(toEnum(form.dateRangeType(), InquiryDateRangeType.class, null))
      .startDateTime(form.startDateTime())
      .endDateTime(form.endDateTime())
      .replyStatus(toEnum(form.replyStatus(), InquiryReplyStatus.class, null))
      .memberRole(toEnum(form.memberRole(), MemberRole.class, null))
      .sortCriteria(toEnum(form.sortCriteria(), InquirySortCriteria.class, null))
      .ascending(form.ascending())
      .build();
  }

}
