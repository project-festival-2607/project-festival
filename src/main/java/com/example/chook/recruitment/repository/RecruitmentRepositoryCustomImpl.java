package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentListCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
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
    RecruitmentListCriteria listCriteria = condition.listCriteria();
    if (listCriteria == null) listCriteria = RecruitmentListCriteria.LATEST;

    if (category != null &&
      category != RecruitmentCategory.INDIVIDUAL &&
      listCriteria.getWageType() != null) {
      throw new IllegalArgumentException("\"개인\"이 아닌 카테고리에서는 급여순 조회를 사용할 수 없습니다.");
    }

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(recruitment.deletedAt.isNull())
      .and(publishedOnlyCondition(condition.publishedOnly()))
      .and(containsAnyKeyword(condition.keywordList()))
      .and(regionSidoEq(condition.regionSidoCode()))
      .and(regionSigunguEq(condition.regionSigunguCode()))
      .and(categoryEq(category))
      .and(statusEq(condition.status()))
      .and(workingStartTimeGoe(condition.workingStartTime()))
      .and(workingEndTimeLoe(condition.workingEndTime()))
      .and(workingStartDateGoe(condition.workingStartDate()))
      .and(workingEndDateLoe(condition.workingEndDate()))
    ;

    JPAQuery<Recruitment> resultQuery = jpaQueryFactory
      .selectFrom(recruitment);
    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(recruitment.count())
      .from(recruitment);

    boolean isWageCriteria = listCriteria.isWageCriteria();
    boolean needsIndividualJoin = isWageCriteria
      || condition.wageType() != null
      || condition.wageValueMin() != null;
    boolean needsFoodTruckJoin = Boolean.TRUE.equals(condition.boothFeeRequired())
      || Boolean.TRUE.equals(condition.electricityProvided())
      || Boolean.TRUE.equals(condition.prepaid());

    if (needsIndividualJoin) {
      resultQuery.join(recruitmentIndividual).on(recruitmentIndividual.recruit.eq(recruitment));
      countQuery.join(recruitmentIndividual).on(recruitmentIndividual.recruit.eq(recruitment));
    }
    if (needsFoodTruckJoin) {
      resultQuery.join(recruitmentFoodTruck).on(recruitmentFoodTruck.recruit.eq(recruitment));
      countQuery.join(recruitmentFoodTruck).on(recruitmentFoodTruck.recruit.eq(recruitment));
    }

    whereCondition
      .and(wageTypeEq(condition.wageType()))
      .and(wageValueBetween(condition.wageValueMin(), condition.wageValueMax()))
      .and(boothFeeRequiredEq(condition.boothFeeRequired()))
      .and(electricityProvidedEq(condition.electricityProvided()))
      .and(prepaidEq(condition.prepaid()));

    if (isWageCriteria) {
      whereCondition.and(recruitmentIndividual.wageType.eq(listCriteria.getWageType()));
    }

    resultQuery.where(whereCondition);
    countQuery.where(whereCondition);

    Long total = countQuery.fetchOne();

    if (isWageCriteria)
      resultQuery.orderBy(recruitmentIndividual.wageValue.desc());
    else {
      switch (listCriteria) {
        case LATEST -> resultQuery.orderBy(recruitment.publishedAt.desc());
        case DEADLINE -> resultQuery.orderBy(recruitment.applicationDeadline.asc());
      }
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

  private BooleanBuilder containsAnyKeyword(List<String> keywords) {

    if (keywords == null || keywords.isEmpty()) return null;

    BooleanBuilder booleanBuilder = new BooleanBuilder();
    for (String keyword : keywords) {
      booleanBuilder.or(
        recruitment.title.contains(keyword)
          .or(recruitment.content.contains(keyword))
      );
    }
    return booleanBuilder;

  }

  private BooleanExpression regionSidoEq(String sidoCode) {
    return sidoCode == null ? null : recruitment.sigungu.sido.code.eq(sidoCode);
  }

  private BooleanExpression regionSigunguEq(String sigunguCode) {
    return sigunguCode == null ? null : recruitment.sigungu.code.eq(sigunguCode);
  }

  private BooleanExpression categoryEq(RecruitmentCategory category) {
    return category == null ? null : recruitment.category.eq(category);
  }

  private BooleanExpression statusEq(RecruitmentStatus status) {
    return status == null ? null : recruitment.status.eq(status);
  }

  private BooleanExpression publishedOnlyCondition(Boolean publishedOnly) {
    return Boolean.TRUE.equals(publishedOnly) ? recruitment.publishedAt.isNotNull() : null;
  }

  private BooleanExpression wageTypeEq(RecruitmentWageType wageType) {
    return wageType == null ? null : recruitmentIndividual.wageType.eq(wageType);
  }

  private BooleanExpression wageValueBetween(Integer min, Integer max) {
    return (min == null || max == null) ? null : recruitmentIndividual.wageValue.between(min, max);
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

  private BooleanExpression workingStartTimeGoe(LocalTime time) {
    return time == null ? null : recruitment.workingStartTime.goe(time);
  }

  private BooleanExpression workingEndTimeLoe(LocalTime time) {
    return time == null ? null : recruitment.workingEndTime.loe(time);
  }

  private BooleanExpression workingStartDateGoe(LocalDate date) {
    return date == null ? null : recruitment.workingStartDate.goe(date);
  }

  private BooleanExpression workingEndDateLoe(LocalDate date) {
    return date == null ? null : recruitment.workingEndDate.loe(date);
  }

}
