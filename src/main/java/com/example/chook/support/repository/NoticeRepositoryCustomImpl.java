package com.example.chook.support.repository;

import com.example.chook.support.entity.Notice;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.chook.support.entity.QNotice.notice;

public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public NoticeRepositoryCustomImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<Notice> search(String searchType, String keyword, Pageable pageable) {
    BooleanExpression condition = keywordCondition(searchType, keyword);

    List<Notice> content = jpaQueryFactory
      .selectFrom(notice)
      .where(condition)
      .orderBy(notice.highlight.desc(), notice.bno.desc())
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = jpaQueryFactory
      .select(notice.count())
      .from(notice)
      .where(condition)
      .fetchOne();

    return new PageImpl<>(content, pageable, total == null ? 0 : total);
  }

  // 검색 카테고리는 제목/내용 두 가지만 지원 (그 외 값이거나 키워드가 없으면 전체 조회)
  private BooleanExpression keywordCondition(String searchType, String keyword) {
    if (keyword == null || keyword.isBlank()) return null;
    return "content".equals(searchType)
      ? notice.content.contains(keyword)
      : notice.title.contains(keyword);
  }

}