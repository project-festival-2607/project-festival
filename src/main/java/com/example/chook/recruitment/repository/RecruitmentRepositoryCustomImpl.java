package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import com.example.chook.recruitment.record.RecruitmentManagementCondition;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.recruitment.entity.QRecruitment.recruitment;
import static com.example.chook.recruitment.entity.QRecruitmentFoodTruck.recruitmentFoodTruck;
import static com.example.chook.recruitment.entity.QRecruitmentIndividual.recruitmentIndividual;

@Slf4j
public class RecruitmentRepositoryCustomImpl implements RecruitmentRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public RecruitmentRepositoryCustomImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<Recruitment> searchRecruitments(RecruitmentSearchCondition condition, Pageable pageable) {

    RecruitmentCategory category = condition.category();

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(recruitment.deletedAt.isNull())
      .and(containsAnyKeyword(condition.keywordList()))
      .and(eq(recruitment.sigungu.sido.code, condition.regionSidoCode()))
      .and(eq(recruitment.sigungu.code, condition.regionSigunguCode()))
      .and(eq(recruitment.category, category))
      .and(eq(recruitment.status, RecruitmentStatus.RECRUITING))
      .and(loe(recruitment.workingStartDate, condition.workingStartDate()))
      .and(goe(recruitment.workingEndDate, condition.workingEndDate()))
      .and(goe(recruitment.workingStartTime, condition.workingStartTime()))
      .and(loe(recruitment.workingEndTime, condition.workingEndTime()))
    ;

    JPAQuery<Recruitment> resultQuery = jpaQueryFactory
      .selectFrom(recruitment);
    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(recruitment.count())
      .from(recruitment);

    boolean needsIndividualJoin = condition.wageType() != null;

    boolean needsFoodTruckJoin = Boolean.TRUE.equals(condition.boothFeeRequired())
      || Boolean.TRUE.equals(condition.electricityProvided())
      || Boolean.TRUE.equals(condition.prepaid());

    if (needsIndividualJoin) {
      resultQuery.join(recruitmentIndividual).on(recruitmentIndividual.recruitment.eq(recruitment));
      countQuery.join(recruitmentIndividual).on(recruitmentIndividual.recruitment.eq(recruitment));
      whereCondition
        .and(eq(recruitmentIndividual.wageType, condition.wageType()));
    }
    if (needsFoodTruckJoin) {
      resultQuery.join(recruitmentFoodTruck).on(recruitmentFoodTruck.recruitment.eq(recruitment));
      countQuery.join(recruitmentFoodTruck).on(recruitmentFoodTruck.recruitment.eq(recruitment));
      whereCondition
        .and(boothFeeRequiredEq(condition.boothFeeRequired()))
        .and(electricityProvidedEq(condition.electricityProvided()))
        .and(prepaidEq(condition.prepaid()));
    }

    resultQuery.where(whereCondition);
    countQuery.where(whereCondition);

    Long total = countQuery.fetchOne();

    // 급여순 정렬을 선택한 경우 우선 정렬
    if (condition.wageType() != null)
      resultQuery.orderBy(recruitmentIndividual.wageValue.desc());

    switch (condition.listCriteria()) {
      case LATEST -> resultQuery.orderBy(recruitment.publishedAt.desc());
      case DEADLINE -> resultQuery.orderBy(recruitment.applicationDeadline.asc());
    }

    List<Recruitment> result = resultQuery
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    return new PageImpl<>(
      result,
      pageable,
      total == null ? 0 : total
    );
  }

  @Override
  public Optional<RecruitmentIndividual> getIndividualById(Long id) {

    RecruitmentIndividual individual = jpaQueryFactory
      .selectFrom(recruitmentIndividual)
      .where(recruitmentIndividual.id.eq(id))
      .fetchOne();

    return Optional.ofNullable(individual);
  }

  @Override
  public Optional<RecruitmentFoodTruck> getFoodTruckById(Long id) {

    RecruitmentFoodTruck foodTruck = jpaQueryFactory
      .selectFrom(recruitmentFoodTruck)
      .where(recruitmentFoodTruck.id.eq(id))
      .fetchOne();

    return Optional.ofNullable(foodTruck);
  }

  @Override
  public Page<Recruitment> searchRecruitments(RecruitmentManagementCondition condition, Pageable pageable) {

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(eq(recruitment.festival.contentId, condition.festivalContentId()))
      .and(eq(recruitment.festival.member.username, condition.festivalUserName()))
      .and(eq(recruitment.category, condition.category()))
      .and(eq(recruitment.status, condition.status()))
      .and(isPublishedEq(condition.isPublished()))
      .and(isDeletedEq(condition.isPublished()))
    ;

    JPAQuery<Recruitment> resultQuery = jpaQueryFactory
      .selectFrom(recruitment);
    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(recruitment.count())
      .from(recruitment);

    resultQuery.where(whereCondition);
    countQuery.where(whereCondition);

    Long total = countQuery.fetchOne();

    switch (condition.listCriteria()) {
      case LATEST -> resultQuery.orderBy(recruitment.publishedAt.desc());
      case DEADLINE -> resultQuery.orderBy(recruitment.applicationDeadline.asc());
    }

    List<Recruitment> result = resultQuery
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    return new PageImpl<>(
      result,
      pageable,
      total == null ? 0 : total
    );

  }

  private BooleanBuilder containsAnyKeyword(List<String> keywords) {

    if (keywords == null || keywords.isEmpty()) return null;

    BooleanBuilder booleanBuilder = new BooleanBuilder();
    for (String keyword : keywords) {
      booleanBuilder.and(
        recruitment.title.contains(keyword)
          .or(recruitment.content.contains(keyword))
          .or(recruitment.sigungu.name.contains(keyword))
          .or(recruitment.sigungu.sido.name.contains(keyword))
          .or(recruitment.sigungu.sido.shortName.contains(keyword))
          .or(recruitment.festival.address.contains(keyword))
          .or(recruitment.festival.eventPlace.contains(keyword))
          .or(recruitment.festival.title.contains(keyword))
          .or(recruitment.festival.overview.contains(keyword))
      );
    }
    return booleanBuilder;

  }

  private BooleanExpression isPublishedEq(Boolean isPublished) {
    return isPublished == null ? null : recruitment.publishedAt.isNotNull().eq(isPublished);
  }

  private BooleanExpression isDeletedEq(Boolean isDeleted) {
    return isDeleted == null ? null : recruitment.deletedAt.isNotNull().eq(isDeleted);
  }

  private BooleanExpression boothFeeRequiredEq(Boolean value) {
    return Boolean.TRUE.equals(value) ? recruitmentFoodTruck.boothFeeRequired.isTrue() : null;
  }

  private BooleanExpression electricityProvidedEq(Boolean value) {
    return Boolean.TRUE.equals(value) ? recruitmentFoodTruck.electricityProvided.isTrue() : null;
  }

  private BooleanExpression prepaidEq(Boolean value) {
    return Boolean.TRUE.equals(value) ? recruitmentFoodTruck.prepaid.isTrue() : null;
  }

}
