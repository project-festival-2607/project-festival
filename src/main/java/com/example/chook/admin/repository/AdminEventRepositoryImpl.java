package com.example.chook.admin.repository;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.festival.FestivalDateRangeType;
import com.example.chook.admin.entity.enums.festival.FestivalKeywordType;
import com.example.chook.admin.entity.enums.festival.FestivalSortCriteria;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.example.chook.admin.util.KeywordUtils.getKeywordCheckFunction;
import static com.example.chook.admin.util.KeywordUtils.getOrderSpecifier;
import static com.example.chook.common.util.QuerydslUtils.goe;
import static com.example.chook.common.util.QuerydslUtils.loe;
import static com.example.chook.festival.QFestival.festival;
import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.recruitment.entity.QRecruitment.recruitment;

@Repository
@Slf4j
public class AdminEventRepositoryImpl implements AdminEventRepository {

  private final JPAQueryFactory jpaQueryFactory;

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
        member.id,
        member.username,
        member.name,
        ExpressionUtils.as(JPAExpressions
            .select(recruitment.id.count())
            .from(recruitment)
            .where(
              recruitment.festival.contentId.eq(festival.contentId),
              recruitment.status.eq(RecruitmentStatus.RECRUITING)
            )
          , "recruitmentCount")

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

  // #################### 테이블 상단 정렬 OrderSpecifier<>[] 구축 메서드 ####################

  private OrderSpecifier<?>[] getFestivalOrderSpecifierArray(FestivalSortCriteria sortCriteria,
                                                             Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case START_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, festival.startDate));
        case END_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, festival.endDate));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, festival.contentId));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }


}
