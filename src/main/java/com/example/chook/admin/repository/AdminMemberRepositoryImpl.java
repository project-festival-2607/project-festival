package com.example.chook.admin.repository;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerDateCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerSortCriteria;
import com.example.chook.admin.condition.JobSeekerSearchCondition;
import com.example.chook.common.util.QuerydslUtils;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.entity.enums.Provider;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.StringPath;
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

import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.member.entity.QJobSeekerProfile.jobSeekerProfile;
import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.member.entity.QMemberSuspension.memberSuspension;

@Repository
@Slf4j
public class AdminMemberRepositoryImpl implements AdminMemberRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminMemberRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<JobSeekerTableDTO> getJobSeekerPage(Pageable pageable, JobSeekerSearchCondition condition) {

    JPAQuery<JobSeekerTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

        JobSeekerTableDTO.class,

        member.id,
        member.username,
        member.name,
        member.phone,
        member.phoneVerified,
        member.email,
        member.role,
        member.status,
        member.createdAt,
        member.updatedAt,
        member.lastLoginAt,
        member.deletedAt,
        member.point,

        jobSeekerProfile.gender,
        jobSeekerProfile.birthDate,
        jobSeekerProfile.streetAddress,
        jobSeekerProfile.detailAddress,

        memberSuspension.updatedAt.as("suspendedAt"),
        memberSuspension.reason.as("suspendedReason")

      ))
      .from(member)
      .join(jobSeekerProfile)
      .on(jobSeekerProfile.memberId.eq(member.id))
      .leftJoin(memberSuspension)
      .on(memberSuspension.memberId.eq(member.id));

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(member.count())
      .from(member)
      .join(jobSeekerProfile)
      .on(jobSeekerProfile.memberId.eq(member.id));

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(member.role.eq(MemberRole.JOB_SEEKER))
      .and(checkKeywordsWithCriteria(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(eq(jobSeekerProfile.gender, condition.gender()))
      .and(checkStatusFilter(condition.status()))
      .and(checkDataRangeCriteria(condition.dateRangeCriteria(),
        condition.startDateTime(),
        condition.endDateTime(),
        condition.startDate(),
        condition.endDate()
      ))
      .and(containsProvider(condition.provider()));

    List<JobSeekerTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
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

  private BooleanExpression containsProvider(Provider provider) {
    return eq(member.socialLogins.any().provider, provider);
  }

  private BooleanBuilder checkStatusFilter(MemberStatusFilter status) {

    BooleanBuilder result = new BooleanBuilder();

    log.info("status: {}", status);

    if (status == null) return null;
    switch (status) {
      case ACTIVE -> result.and(eq(member.status, MemberStatus.ACTIVE));
      case DORMANT -> result.and(eq(member.status, MemberStatus.DORMANT));
      case SUSPENDED -> result.and(eq(member.status, MemberStatus.SUSPENDED));
      case DELETED -> result.and(member.deletedAt.isNotNull());
    }

    return result;
  }

  private BooleanBuilder checkDataRangeCriteria(JobSeekerDateCriteria criteria,
                                                LocalDateTime startDateTime,
                                                LocalDateTime endDateTime,
                                                LocalDate startDate,
                                                LocalDate endDate) {

    if (criteria == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (criteria) {
      case CREATED_AT -> {
        result.and(goe(member.createdAt, startDateTime));
        result.and(loe(member.createdAt, endDateTime));
      }
      case UPDATED_AT -> {
        result.and(goe(member.updatedAt, startDateTime));
        result.and(loe(member.updatedAt, endDateTime));
      }
      case LAST_LOGIN_AT -> {
        result.and(goe(member.lastLoginAt, startDateTime));
        result.and(loe(member.lastLoginAt, endDateTime));
      }
      case DELETED_AT -> {
        result.and(member.deletedAt.isNotNull());
        result.and(goe(member.deletedAt, startDateTime));
        result.and(loe(member.deletedAt, endDateTime));
      }
      case BIRTH_DATE -> {
        result.and(goe(jobSeekerProfile.birthDate, startDate));
        result.and(lt(jobSeekerProfile.birthDate, endDate));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }

  private BooleanBuilder checkKeywordsWithCriteria(List<String> keywordList,
                                                   JobSeekerKeywordType keywordType,
                                                   KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<JobSeekerKeywordType> types =
      keywordType == null
        ? List.of(JobSeekerKeywordType.values())
        : List.of(keywordType);

    log.info("types: {}", types);
    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringPath, String, BooleanExpression> keywordCheck =
      keywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (JobSeekerKeywordType type : types) {
        switch (type) {
          case USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
          case PHONE -> keywordResult.or(keywordCheck.apply(member.phone, keyword.replace("-", "")));
          case EMAIL -> keywordResult.or(keywordCheck.apply(member.email, keyword));
          case ADDRESS -> {
            keywordResult.or(contains(jobSeekerProfile.streetAddress, keyword));
            keywordResult.or(contains(jobSeekerProfile.detailAddress, keyword));
          }
        }
      }
      result.and(keywordResult);
    }
    return result;
  }

  private BiFunction<StringPath, String, BooleanExpression> keywordCheckFunction(KeywordCriteria keywordCriteria) {
    return (keywordCriteria == KeywordCriteria.EQUALS)
      ? QuerydslUtils::eq
      : QuerydslUtils::contains;
  }

  private OrderSpecifier<?>[] getOrderSpecifierArray(JobSeekerSortCriteria sortCriteria,
                                                     Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.createdAt));
        case UPDATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.updatedAt));
        case LAST_LOGIN_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.lastLoginAt));
        case DELETED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.deletedAt));
        case BIRTH_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, jobSeekerProfile.birthDate));
        case POINT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.point));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, member.id));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private <T extends Comparable<? super T>> List<OrderSpecifier<?>> getOrderSpecifier(
    Order order,
    ComparableExpressionBase<T> expression) {

    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    orderSpecifiers.add(
      new OrderSpecifier<>(
        Order.ASC,
        new CaseBuilder()
          .when(expression.isNull()).then(1)
          .otherwise(0)
      )
    );
    orderSpecifiers.add(new OrderSpecifier<>(order, expression));
    return orderSpecifiers;

  }
}
