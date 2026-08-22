package com.example.chook.admin.repository;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.condition.board.NoticeSearchCondition;
import com.example.chook.admin.dto.board.InquiryTableDTO;
import com.example.chook.admin.dto.board.NoticeTableDTO;
import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.board.inquiry.InquiryDateRangeType;
import com.example.chook.admin.enums.board.inquiry.InquiryKeywordType;
import com.example.chook.admin.enums.board.inquiry.InquiryReplyStatus;
import com.example.chook.admin.enums.board.inquiry.InquirySortCriteria;
import com.example.chook.admin.enums.board.notice.NoticeDateRangeType;
import com.example.chook.admin.enums.board.notice.NoticeSortCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCheckFunction;
import static com.example.chook.admin.util.KeywordUtils.getOrderSpecifier;
import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.support.entity.QInquiry.inquiry;
import static com.example.chook.support.entity.QNotice.notice;

@Repository
@Slf4j
public class AdminBoardRepositoryImpl implements AdminBoardRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminBoardRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<InquiryTableDTO> getInquiryListPage(Pageable pageable, InquirySearchCondition condition) {

    JPAQuery<InquiryTableDTO> resultQuery;
    resultQuery = this.jpaQueryFactory.select(Projections.fields(

        InquiryTableDTO.class,

        inquiry.ino,
        inquiry.title,
        inquiry.createdAt,
        inquiry.updatedAt,
        inquiry.commentTime.as("repliedAt"),

        member.id.as("memberId"),
        member.username.as("memberUsername"),
        member.name.as("memberName"),
        member.role.as("memberRole")

      ))
      .from(inquiry)
      .leftJoin(member).on(inquiry.id.eq(member.id));

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(inquiry.count())
      .from(inquiry)
      .leftJoin(member).on(inquiry.id.eq(member.id));

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applyInquiryKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyReplyStatusFilter(condition.replyStatus()))
      .and(eq(member.role, condition.memberRole()))
      .and(applyRecruiterDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime()
      ));

    List<InquiryTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getInquiryOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );

  }

  @Override
  public Page<NoticeTableDTO> getNoticeListPage(Pageable pageable, NoticeSearchCondition condition) {

    JPAQuery<NoticeTableDTO> resultQuery;
    resultQuery = this.jpaQueryFactory.select(Projections.fields(

        NoticeTableDTO.class,

        notice.bno,
        notice.title,
        notice.createdAt,
        notice.highlight

      ))
      .from(notice);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(notice.count())
      .from(notice);

    BooleanBuilder whereCondition = new BooleanBuilder()
      .and(eq(notice.highlight, condition.highlight()))
      .and(applyNoticeDateRangeFilter(
        condition.dateRangeType(),
        condition.startDate(),
        condition.endDate()
      ));

    List<NoticeTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getNoticeOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    Long total = countQuery
      .where(whereCondition)
      .fetchOne();

    return new PageImpl<>(
      content,
      pageable,
      total != null ? total : 0
    );

  }

  // #################### KEYWORD FILTER 적용 메서드 ####################

  private BooleanBuilder applyInquiryKeywordFilter(List<String> keywordList,
                                                   InquiryKeywordType keywordType,
                                                   KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<InquiryKeywordType> types =
      keywordType == null
        ? List.of(InquiryKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (InquiryKeywordType type : types) {
        switch (type) {
          case INO -> {
            StringExpression ino = Expressions.stringTemplate("STR({0})", inquiry.ino);
            keywordResult.or(keywordCheck.apply(ino, keyword));
          }
          case TITLE -> keywordResult.or(keywordCheck.apply(inquiry.title, keyword));
          case CONTENT -> keywordResult.or(keywordCheck.apply(inquiry.content, keyword));
          case REPLY -> keywordResult.or(keywordCheck.apply(inquiry.comment, keyword));
          case USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  // #################### DATE RANGE FILTER 적용 메서드 ####################

  private BooleanBuilder applyRecruiterDateRangeFilter(InquiryDateRangeType dateRangeType,
                                                       LocalDateTime startDateTime,
                                                       LocalDateTime endDateTime) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
      case CREATED_AT -> {
        result.and(goe(inquiry.createdAt, startDateTime));
        result.and(loe(inquiry.createdAt, endDateTime));
      }
      case UPDATED_AT -> {
        result.and(goe(inquiry.updatedAt, startDateTime));
        result.and(loe(inquiry.updatedAt, endDateTime));
      }
      case REPLIED_AT -> {
        result.and(goe(inquiry.commentTime, startDateTime));
        result.and(loe(inquiry.commentTime, endDateTime));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }

  private BooleanBuilder applyNoticeDateRangeFilter(NoticeDateRangeType dateRangeType,
                                                    LocalDate startDate,
                                                    LocalDate endDate) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTimeExclusive = endDate.plusDays(1).atStartOfDay();

    switch (dateRangeType) {
      case CREATED_AT -> {
        result.and(goe(notice.createdAt, startDateTime));
        result.and(lt(notice.createdAt, endDateTimeExclusive));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }


  // #################### 테이블 상단 필터 적용 메서드 ####################

  private BooleanBuilder applyReplyStatusFilter(InquiryReplyStatus replyStatus) {

    BooleanBuilder result = new BooleanBuilder();

    if (replyStatus == null) return null;

    switch (replyStatus) {
      case REPLIED -> result.and(inquiry.commentTime.isNotNull());
      case NOT_REPLIED -> result.and(inquiry.commentTime.isNull());
    }

    return result;

  }


  // #################### 테이블 상단 정렬 OrderSpecifier<>[] 구축 메서드 ####################

  private OrderSpecifier<?>[] getInquiryOrderSpecifierArray(InquirySortCriteria sortCriteria,
                                                            Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, inquiry.createdAt));
        case UPDATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, inquiry.updatedAt));
        case REPLIED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, inquiry.commentTime));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, inquiry.ino));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private OrderSpecifier<?>[] getNoticeOrderSpecifierArray(NoticeSortCriteria sortCriteria,
                                                           Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, notice.createdAt));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, notice.bno));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }
}
