package com.example.chook.support.repository;

import com.example.chook.support.entity.Inquiry;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.example.chook.support.entity.QInquiry.inquiry;

public class InquiryRepositoryCustomImpl implements InquiryRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public InquiryRepositoryCustomImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<Inquiry> search(String searchType, String keyword, String answered) {
    BooleanBuilder condition = new BooleanBuilder();
    condition.and(keywordCondition(searchType, keyword));
    condition.and(answeredCondition(answered));

    return jpaQueryFactory
      .selectFrom(inquiry)
      .where(condition)
      .orderBy(inquiry.createdAt.asc())
      .fetch();
  }

  // 검색 카테고리는 아이디(정확히 일치)/제목(포함) 두 가지만 지원
  private BooleanExpression keywordCondition(String searchType, String keyword) {
    if (keyword == null || keyword.isBlank()) return null;

    if ("ino".equals(searchType)) {
      try {
        return inquiry.ino.eq(Long.parseLong(keyword.trim()));
      } catch (NumberFormatException e) {
        return inquiry.ino.eq(-1L); // 숫자가 아니면 결과 없음
      }
    }

    return inquiry.title.contains(keyword);
  }

  private BooleanExpression answeredCondition(String answered) {
    if ("done".equals(answered)) return inquiry.comment.isNotNull();
    if ("pending".equals(answered)) return inquiry.comment.isNull();
    return null;
  }

}