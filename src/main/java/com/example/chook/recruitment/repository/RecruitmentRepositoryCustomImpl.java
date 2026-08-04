package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.chook.recruitment.entity.QRecruitment.recruitment;
import static com.example.chook.recruitment.entity.QRecruitmentIndividual.recruitmentIndividual;

@Slf4j
public class RecruitmentRepositoryCustomImpl implements RecruitmentRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public RecruitmentRepositoryCustomImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<Recruitment> searchRecruitments(RecruitmentSearchCondition condition) {

    BooleanBuilder booleanBuilder = new BooleanBuilder();

    booleanBuilder
      .and(recruitment.deletedAt.isNull())
      .and(recruitment.published)
      .and(containsAnyKeyword(condition.keywords()))
      .and(regionSidoEq(condition.regionSidoCode()))
      .and(regionSigunguEq(condition.regionSigunguCode()))
      .and(categoryEq(condition.category()))
      .and(statusEq(condition.status()))
      .and(workingStartTimeGoe(condition.workingStartTime()))
      .and(workingEndTimeLoe(condition.workingEndTime()))
      .and(workingStartDateGoe(condition.workingStartDate()))
      .and(workingEndDateLoe(condition.workingEndDate()))
    ;

    JPAQuery<Recruitment> jpaQuery = jpaQueryFactory
      .selectFrom(recruitment);

    boolean wageTypeSpecified =
      condition.category() == RecruitmentCategory.INDIVIDUAL &&
      condition.wageType() != null;

    if (wageTypeSpecified) {
      jpaQuery
        .join(recruitmentIndividual)
        .on(recruitmentIndividual.recruit.eq(recruitment));
      booleanBuilder
        .and(recruitmentIndividual.wageType.eq(condition.wageType()));
    }

    jpaQuery.where(booleanBuilder);
    if (wageTypeSpecified)
      jpaQuery.orderBy(recruitmentIndividual.wageValue.desc());

    return jpaQuery.fetch();
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
