package com.example.chook.admin.repository;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.dto.board.AdminInquiryTableDTO;
import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.inquiry.InquiryDateRangeType;
import com.example.chook.admin.entity.enums.inquiry.InquiryKeywordType;
import com.example.chook.admin.entity.enums.inquiry.InquiryReplyStatus;
import com.example.chook.admin.entity.enums.inquiry.InquirySortCriteria;
import com.example.chook.member.entity.enums.MemberRole;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCheckFunction;
import static com.example.chook.admin.util.KeywordUtils.getOrderSpecifier;
import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.support.entity.QInquiry.inquiry;

@Repository
@Slf4j
public class AdminBoardRepositoryImpl implements AdminBoardRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminBoardRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<AdminInquiryTableDTO> getInquiryListPage(Pageable pageable, InquirySearchCondition condition) {

    JPAQuery<AdminInquiryTableDTO> resultQuery;
    resultQuery = this.jpaQueryFactory.select(Projections.fields(

        AdminInquiryTableDTO.class,

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

    List<AdminInquiryTableDTO> content = resultQuery
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
            StringExpression ino = Expressions.stringTemplate("CAST({0} AS CHAR)", inquiry.ino);
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
}
