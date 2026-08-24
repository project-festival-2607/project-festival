package com.example.chook.admin.repository;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.condition.event.RecruitmentSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.dto.event.RecruitmentTableDTO;
import com.example.chook.admin.enums.KeywordCriteria;
import com.example.chook.admin.enums.event.festival.FestivalDateRangeType;
import com.example.chook.admin.enums.event.festival.FestivalKeywordType;
import com.example.chook.admin.enums.event.festival.FestivalSortCriteria;
import com.example.chook.admin.enums.event.recruitment.RecruitmentDateRangeType;
import com.example.chook.admin.enums.event.recruitment.RecruitmentKeywordType;
import com.example.chook.admin.enums.event.recruitment.RecruitmentSortCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
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
import static com.example.chook.application.entity.QApplication.application;
import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.festival.QFestival.festival;
import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.recruitment.entity.QRecruitment.recruitment;
import static com.example.chook.region.entity.QRegionSigungu.regionSigungu;

@Repository
@Slf4j
public class AdminEventRepositoryImpl implements AdminEventRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final NumberExpression<Long> recruitmentCount = Expressions.numberTemplate(
    Long.class,
    "({0})",
    JPAExpressions
      .select(recruitment.id.count())
      .from(recruitment)
      .where(
        recruitment.festival.contentId.eq(festival.contentId),
        recruitment.status.eq(RecruitmentStatus.RECRUITING)
      )
  );
  private final NumberExpression<Long> applicantCount = Expressions.numberTemplate(
    Long.class,
    "({0})",
    JPAExpressions
      .select(application.id.count())
      .from(application)
      .where(
        application.recruitment.id.eq(recruitment.id)
      )
  );

  public AdminEventRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<FestivalTableDTO> getFestivalPage(Pageable pageable, FestivalSearchCondition condition) {

    JPAQuery<FestivalTableDTO> resultQuery;
    resultQuery = this.jpaQueryFactory.select(Projections.fields(

        FestivalTableDTO.class,

        festival.contentId,
        festival.title,
        festival.address,
        festival.eventPlace,
        festival.zipCode,
        festival.startDate,
        festival.endDate,
        member.id.as("memberId"),
        member.username.as("memberUsername"),
        member.name.as("memberName"),
        ExpressionUtils.as(recruitmentCount, "recruitmentCount")

      ))
      .from(festival)
      .leftJoin(member).on(festival.member.id.eq(member.id));

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(festival.count())
      .from(festival)
      .leftJoin(member).on(festival.member.id.eq(member.id));

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applyFestivalKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyFestivalDateRangeFilter(
        condition.dateRangeType(),
        condition.startDate(),
        condition.endDate()
      ));

    List<FestivalTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getFestivalOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
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
  public Page<RecruitmentTableDTO> getRecruitmentPage(Pageable pageable, RecruitmentSearchCondition condition) {

    JPAQuery<RecruitmentTableDTO> resultQuery;
    resultQuery = this.jpaQueryFactory.select(Projections.fields(

        RecruitmentTableDTO.class,

        recruitment.id,
        recruitment.title,
        recruitment.category,
        festival.contentId.as("festivalContentId"),
        festival.title.as("festivalTitle"),

        regionSigungu.sido.code.as("sidoCode"),
        regionSigungu.sido.name.as("sidoName"),
        regionSigungu.code.as("sigunguCode"),
        regionSigungu.name.as("sigunguName"),
        recruitment.workingLocation,

        recruitment.applicationDeadline,
        recruitment.workingStartDate,
        recruitment.workingEndDate,

        recruitment.status,
        recruitment.publishedAt,
        recruitment.deletedAt,
        ExpressionUtils.as(applicantCount, "applicantCount")

      ))
      .from(recruitment);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(recruitment.count())
      .from(recruitment);

    resultQuery = applyRecruitmentJoin(resultQuery);
    countQuery = applyRecruitmentJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(applyRecruitmentKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(eq(recruitment.category, condition.category()))
      .and(eq(regionSigungu.sido.code, condition.sidoCode()))
      .and(eq(regionSigungu.code, condition.sigunguCode()))
      .and(eq(recruitment.status, condition.status()))
      .and(applyRecruitmentDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime(),
        condition.startDate(),
        condition.endDate()
      ));

    List<RecruitmentTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getRecruitmentOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
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

  // #################### JOIN 적용 메서드 ####################

  private <T> JPAQuery<T> applyRecruitmentJoin(JPAQuery<T> query) {
    return query
      .leftJoin(festival)
      .on(recruitment.festival.contentId.eq(festival.contentId))
      .leftJoin(regionSigungu)
      .on(recruitment.sigungu.code.eq(regionSigungu.code))
      .leftJoin(member)
      .on(festival.member.id.eq(member.id))
      ;
  }

  // #################### KEYWORD FILTER 적용 메서드 ####################

  private BooleanBuilder applyFestivalKeywordFilter(List<String> keywordList,
                                                    FestivalKeywordType keywordType,
                                                    KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<FestivalKeywordType> types =
      keywordType == null
        ? List.of(FestivalKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (FestivalKeywordType type : types) {
        switch (type) {
          case CONTENT_ID -> keywordResult.or(keywordCheck.apply(festival.contentId, keyword));
          case TITLE -> keywordResult.or(keywordCheck.apply(festival.title, keyword));
          case PLACE -> {
            keywordResult.or(keywordCheck.apply(festival.eventPlace, keyword));
            keywordResult.or(keywordCheck.apply(festival.address, keyword));
          }
          case CONTENT -> {
            keywordResult.or(keywordCheck.apply(festival.homepage, keyword));
            keywordResult.or(keywordCheck.apply(festival.overview, keyword));
            keywordResult.or(keywordCheck.apply(festival.tel, keyword));
            keywordResult.or(keywordCheck.apply(festival.telName, keyword));
            keywordResult.or(keywordCheck.apply(festival.program, keyword));
          }
          case USERNAME -> {
            keywordResult.or(keywordCheck.apply(member.username, keyword));
          }
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  private BooleanBuilder applyRecruitmentKeywordFilter(List<String> keywordList,
                                                       RecruitmentKeywordType keywordType,
                                                       KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<RecruitmentKeywordType> types =
      keywordType == null
        ? List.of(RecruitmentKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringExpression, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (RecruitmentKeywordType type : types) {
        switch (type) {
          case RECRUITMENT_ID -> {
            StringExpression id = Expressions.stringTemplate("STR({0})", recruitment.id);
            keywordResult.or(keywordCheck.apply(id, keyword));
          }
          case RECRUITMENT_TITLE -> keywordResult.or(keywordCheck.apply(recruitment.title, keyword));
          case RECRUITMENT_CONTENT -> keywordResult.or(keywordCheck.apply(recruitment.content, keyword));
          case FESTIVAL_ID -> keywordResult.or(keywordCheck.apply(festival.contentId, keyword));
          case FESTIVAL_TITLE -> keywordResult.or(keywordCheck.apply(festival.title, keyword));
          case MEMBER_ID -> {
            StringExpression memberId = Expressions.stringTemplate("STR({0})", member.id);
            keywordResult.or(keywordCheck.apply(memberId, keyword));
          }
          case LOCATION -> {
            keywordResult.or(keywordCheck.apply(regionSigungu.sido.name, keyword));
            keywordResult.or(keywordCheck.apply(regionSigungu.name, keyword));
            keywordResult.or(keywordCheck.apply(recruitment.workingLocation, keyword));
          }
        }
      }
      result.and(keywordResult);
    }
    return result;

  }

  // #################### DATE RANGE FILTER 적용 메서드 ####################

  private BooleanBuilder applyFestivalDateRangeFilter(FestivalDateRangeType dateRangeType,
                                                      LocalDate startDate,
                                                      LocalDate endDate) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
      case OVERLAPPING -> {
        result.and(loe(festival.startDate, endDate));
        result.and(goe(festival.endDate, startDate));
      }
      case CONTAINED_IN -> {
        result.and(goe(festival.startDate, startDate));
        result.and(loe(festival.endDate, endDate));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;

  }

  private BooleanBuilder applyRecruitmentDateRangeFilter(RecruitmentDateRangeType dateRangeType,
                                                         LocalDateTime startDateTime,
                                                         LocalDateTime endDateTime,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
      case PUBLISHED_AT -> {
        result.and(goe(recruitment.publishedAt, startDateTime));
        result.and(loe(recruitment.publishedAt, endDateTime));
      }
      case DELETED_AT -> {
        result.and(goe(recruitment.deletedAt, startDateTime));
        result.and(loe(recruitment.deletedAt, endDateTime));
      }
      case APPLICATION_DEADLINE -> {
        result.and(goe(recruitment.applicationDeadline, startDate));
        result.and(loe(recruitment.applicationDeadline, endDate));
      }
      case WORKING_START_DATE -> {
        result.and(goe(recruitment.workingStartDate, startDate));
        result.and(loe(recruitment.workingStartDate, endDate));
      }
      case WORKING_END_DATE -> {
        result.and(goe(recruitment.workingEndDate, startDate));
        result.and(loe(recruitment.workingEndDate, endDate));
      }
      case WORK_DURATION_OVERLAPPING -> {
        result.and(loe(recruitment.workingStartDate, endDate));
        result.and(goe(recruitment.workingEndDate, startDate));
      }
      case WORK_DURATION_CONTAINED_IN -> {
        result.and(goe(recruitment.workingStartDate, startDate));
        result.and(loe(recruitment.workingEndDate, endDate));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;

  }

  // #################### 테이블 상단 정렬 OrderSpecifier<>[] 구축 메서드 ####################

  private OrderSpecifier<?>[] getFestivalOrderSpecifierArray(FestivalSortCriteria sortCriteria,
                                                             Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case START_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, festival.startDate));
        case END_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, festival.endDate));
        case RECRUITMENT_COUNT -> orderSpecifiers.addAll(getOrderSpecifier(order, recruitmentCount));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, festival.contentId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private OrderSpecifier<?>[] getRecruitmentOrderSpecifierArray(RecruitmentSortCriteria sortCriteria,
                                                                Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case APPLICATION_DEADLINE -> orderSpecifiers.addAll(getOrderSpecifier(order, recruitment.applicationDeadline));
        case WORKING_START_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, recruitment.workingStartDate));
        case WORKING_END_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, recruitment.workingEndDate));
        case PUBLISHED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, recruitment.publishedAt));
        case DELETED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, recruitment.deletedAt));
        case APPLICANT_COUNT -> orderSpecifiers.addAll(getOrderSpecifier(order, applicantCount));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, recruitment.id));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }
}
