package com.example.chook.admin.repository;

import com.example.chook.admin.condition.JobEquipSearchCondition;
import com.example.chook.admin.condition.JobSeekerSearchCondition;
import com.example.chook.admin.condition.RecruiterSearchCondition;
import com.example.chook.admin.dto.JobEquipTableDTO;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.dto.RecruiterTableDTO;
import com.example.chook.admin.entity.enums.KeywordCriteria;
import com.example.chook.admin.entity.enums.MemberStatusFilter;
import com.example.chook.admin.entity.enums.jobequip.JobEquipDateRangeType;
import com.example.chook.admin.entity.enums.jobequip.JobEquipKeywordType;
import com.example.chook.admin.entity.enums.jobequip.JobEquipSortCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerDateRangeType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerSortCriteria;
import com.example.chook.admin.entity.enums.recruiter.RecruiterDateRangeType;
import com.example.chook.admin.entity.enums.recruiter.RecruiterKeywordType;
import com.example.chook.admin.entity.enums.recruiter.RecruiterSortCriteria;
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
import static com.example.chook.member.entity.QBusinessRegistration.businessRegistration;
import static com.example.chook.member.entity.QEmployerProfile.employerProfile;
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
        memberSuspension.updatedAt.as("suspendedAt"),
        memberSuspension.reason.as("suspendedReason"),

        jobSeekerProfile.gender,
        jobSeekerProfile.birthDate,
        jobSeekerProfile.streetAddress,
        jobSeekerProfile.detailAddress

      ))
      .from(member);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(member.count())
      .from(member);

    resultQuery = applyJobSeekerJoin(resultQuery);
    countQuery = applyJobSeekerJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(member.role.eq(MemberRole.JOB_SEEKER))
      .and(applyJobSeekerKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyStatusFilter(condition.status()))
      .and(applyJobSeekerDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime(),
        condition.startDate(),
        condition.endDate()
      ))
      .and(eq(jobSeekerProfile.gender, condition.gender()))
      .and(applyProviderFilter(condition.provider()));

    List<JobSeekerTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getJobSeekerOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
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
  public Page<JobEquipTableDTO> getJobEquipPage(Pageable pageable, JobEquipSearchCondition condition) {

    JPAQuery<JobEquipTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

        JobEquipTableDTO.class,

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
        memberSuspension.updatedAt.as("suspendedAt"),
        memberSuspension.reason.as("suspendedReason"),

        jobSeekerProfile.gender,
        jobSeekerProfile.birthDate,
        jobSeekerProfile.streetAddress,
        jobSeekerProfile.detailAddress,

        businessRegistration.businessNumber,
        businessRegistration.verifiedAt.as("businessNumberVerifiedAt")

      ))
      .from(member);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(member.count())
      .from(member);

    resultQuery = applyJobEquipJoin(resultQuery);
    countQuery = applyJobEquipJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(member.role.eq(MemberRole.JOB_EQUIP))
      .and(applyJobEquipKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyStatusFilter(condition.status()))
      .and(applyJobEquipDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime(),
        condition.startDate(),
        condition.endDate()
      ))
      .and(eq(jobSeekerProfile.gender, condition.gender()))
      .and(applyProviderFilter(condition.provider()));

    List<JobEquipTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getJobEquipOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
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
  public Page<RecruiterTableDTO> getRecruiterPage(Pageable pageable, RecruiterSearchCondition condition) {

    JPAQuery<RecruiterTableDTO> resultQuery = this.jpaQueryFactory.select(Projections.fields(

        RecruiterTableDTO.class,

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
        memberSuspension.updatedAt.as("suspendedAt"),
        memberSuspension.reason.as("suspendedReason"),

        employerProfile.companyName,
        employerProfile.ceoName,
        employerProfile.foundedAt,
        employerProfile.streetAddress,
        employerProfile.detailAddress,

        businessRegistration.businessNumber,
        businessRegistration.verifiedAt.as("businessNumberVerifiedAt")

      ))
      .from(member);

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(member.count())
      .from(member);

    resultQuery = applyRecruiterJoin(resultQuery);
    countQuery = applyRecruiterJoin(countQuery);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(member.role.eq(MemberRole.RECRUITER))
      .and(applyRecruiterKeywordFilter(condition.keywordList(), condition.keywordType(), condition.keywordCriteria()))
      .and(applyStatusFilter(condition.status()))
      .and(applyRecruiterDateRangeFilter(
        condition.dateRangeType(),
        condition.startDateTime(),
        condition.endDateTime(),
        condition.startDate(),
        condition.endDate()
      ));

    List<RecruiterTableDTO> content = resultQuery
      .where(whereCondition)
      .orderBy(getRecruiterOrderSpecifierArray(condition.sortCriteria(), condition.ascending()))
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

  private <T> JPAQuery<T> applyJobSeekerJoin(JPAQuery<T> query) {
    return query
      .leftJoin(memberSuspension)
      .on(memberSuspension.memberId.eq(member.id))
      .join(jobSeekerProfile)
      .on(jobSeekerProfile.memberId.eq(member.id));
  }

  private <T> JPAQuery<T> applyJobEquipJoin(JPAQuery<T> query) {
    return query
      .leftJoin(memberSuspension)
      .on(memberSuspension.memberId.eq(member.id))
      .join(jobSeekerProfile)
      .on(jobSeekerProfile.memberId.eq(member.id))
      .leftJoin(businessRegistration)
      .on(businessRegistration.memberId.eq(member.id));
  }

  private <T> JPAQuery<T> applyRecruiterJoin(JPAQuery<T> query) {
    return query
      .leftJoin(memberSuspension)
      .on(memberSuspension.memberId.eq(member.id))
      .join(employerProfile)
      .on(employerProfile.memberId.eq(member.id))
      .leftJoin(businessRegistration)
      .on(businessRegistration.memberId.eq(member.id));
  }


  // #################### KEYWORD FILTER 적용 메서드 ####################

  private BooleanBuilder applyJobSeekerKeywordFilter(List<String> keywordList,
                                                     JobSeekerKeywordType keywordType,
                                                     KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<JobSeekerKeywordType> types =
      keywordType == null
        ? List.of(JobSeekerKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringPath, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

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

  private BooleanBuilder applyJobEquipKeywordFilter(List<String> keywordList,
                                                    JobEquipKeywordType keywordType,
                                                    KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<JobEquipKeywordType> types =
      keywordType == null
        ? List.of(JobEquipKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringPath, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (JobEquipKeywordType type : types) {
        switch (type) {
          case USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
          case PHONE -> keywordResult.or(keywordCheck.apply(member.phone, keyword.replace("-", "")));
          case EMAIL -> keywordResult.or(keywordCheck.apply(member.email, keyword));
          case BUSINESS_NUMBER -> keywordResult.or(keywordCheck.apply(businessRegistration.businessNumber, keyword));
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

  private BooleanBuilder applyRecruiterKeywordFilter(List<String> keywordList,
                                                     RecruiterKeywordType keywordType,
                                                     KeywordCriteria keywordCriteria) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<RecruiterKeywordType> types =
      keywordType == null
        ? List.of(RecruiterKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    BiFunction<StringPath, String, BooleanExpression> keywordCheck =
      getKeywordCheckFunction(keywordCriteria);

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (RecruiterKeywordType type : types) {
        switch (type) {
          case USERNAME -> keywordResult.or(keywordCheck.apply(member.username, keyword));
          case NAME -> keywordResult.or(keywordCheck.apply(member.name, keyword));
          case PHONE -> keywordResult.or(keywordCheck.apply(member.phone, keyword));
          case EMAIL -> keywordResult.or(keywordCheck.apply(member.email, keyword));
          case COMPANY_NAME -> keywordResult.or(keywordCheck.apply(employerProfile.companyName, keyword));
          case CEO_NAME -> keywordResult.or(keywordCheck.apply(employerProfile.ceoName, keyword));
          case BUSINESS_NUMBER ->
            keywordResult.or(keywordCheck.apply(businessRegistration.businessNumber, keyword.replace("-", "")));
          case ADDRESS -> {
            keywordResult.or(contains(employerProfile.streetAddress, keyword));
            keywordResult.or(contains(employerProfile.detailAddress, keyword));
          }
        }
      }
      result.and(keywordResult);
    }
    return result;
  }

  private BiFunction<StringPath, String, BooleanExpression> getKeywordCheckFunction(KeywordCriteria keywordCriteria) {
    return (keywordCriteria == KeywordCriteria.EXACT)
      ? QuerydslUtils::eq
      : QuerydslUtils::contains;
  }


  // #################### DATE RANGE FILTER 적용 메서드 ####################

  private BooleanBuilder applyJobSeekerDateRangeFilter(JobSeekerDateRangeType dateRangeType,
                                                       LocalDateTime startDateTime,
                                                       LocalDateTime endDateTime,
                                                       LocalDate startDate,
                                                       LocalDate endDate) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
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
        result.and(loe(jobSeekerProfile.birthDate, endDate));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }

  private BooleanBuilder applyJobEquipDateRangeFilter(JobEquipDateRangeType dateRangeType,
                                                      LocalDateTime startDateTime,
                                                      LocalDateTime endDateTime,
                                                      LocalDate startDate,
                                                      LocalDate endDate) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
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
        result.and(loe(jobSeekerProfile.birthDate, endDate));
      }
      case BUSINESS_NUMBER_VERIFIED_AT -> {
        result.and(goe(businessRegistration.verifiedAt, startDateTime));
        result.and(loe(businessRegistration.verifiedAt, startDateTime));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }

  private BooleanBuilder applyRecruiterDateRangeFilter(RecruiterDateRangeType dateRangeType,
                                                       LocalDateTime startDateTime,
                                                       LocalDateTime endDateTime,
                                                       LocalDate startDate,
                                                       LocalDate endDate) {

    if (dateRangeType == null) return null;

    BooleanBuilder result = new BooleanBuilder();

    switch (dateRangeType) {
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
      case FOUNDED_AT -> {
        result.and(goe(employerProfile.foundedAt, startDate));
        result.and(loe(employerProfile.foundedAt, endDate));
      }
      case BUSINESS_NUMBER_VERIFIED_AT -> {
        result.and(goe(businessRegistration.verifiedAt, startDateTime));
        result.and(loe(businessRegistration.verifiedAt, endDateTime));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }


  // #################### 테이블 상단 필터 적용 메서드 ####################

  private BooleanBuilder applyStatusFilter(MemberStatusFilter status) {

    BooleanBuilder result = new BooleanBuilder();

    if (status == null) return null;
    switch (status) {
      case ACTIVE -> result.and(eq(member.status, MemberStatus.ACTIVE));
      case DORMANT -> result.and(eq(member.status, MemberStatus.DORMANT));
      case SUSPENDED -> result.and(eq(member.status, MemberStatus.SUSPENDED));
      case DELETED -> result.and(member.deletedAt.isNotNull());
    }

    return result;
  }

  private BooleanExpression applyProviderFilter(Provider provider) {
    return eq(member.socialLogins.any().provider, provider);
  }


  // #################### 테이블 상단 정렬 OrderSpecifier<>[] 구축 메서드 ####################

  private OrderSpecifier<?>[] getJobSeekerOrderSpecifierArray(JobSeekerSortCriteria sortCriteria,
                                                              Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.createdAt));
        case UPDATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.updatedAt));
        case LAST_LOGIN_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.lastLoginAt));
        case DELETED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.deletedAt));
        case POINT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.point));
        case BIRTH_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, jobSeekerProfile.birthDate));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, member.id));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private OrderSpecifier<?>[] getJobEquipOrderSpecifierArray(JobEquipSortCriteria sortCriteria,
                                                             Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.createdAt));
        case UPDATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.updatedAt));
        case LAST_LOGIN_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.lastLoginAt));
        case DELETED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.deletedAt));
        case POINT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.point));
        case BIRTH_DATE -> orderSpecifiers.addAll(getOrderSpecifier(order, jobSeekerProfile.birthDate));
        case BUSINESS_NUMBER_VERIFIED_AT ->
          orderSpecifiers.addAll(getOrderSpecifier(order, businessRegistration.verifiedAt));
      }
    }
    orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, member.id));
    return orderSpecifiers.toArray(new OrderSpecifier[0]);
  }

  private OrderSpecifier<?>[] getRecruiterOrderSpecifierArray(RecruiterSortCriteria sortCriteria,
                                                              Boolean ascending) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
    if (sortCriteria != null) {
      Order order = ascending ? Order.ASC : Order.DESC;
      switch (sortCriteria) {
        case CREATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.createdAt));
        case UPDATED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.updatedAt));
        case LAST_LOGIN_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.lastLoginAt));
        case DELETED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.deletedAt));
        case POINT -> orderSpecifiers.addAll(getOrderSpecifier(order, member.point));
        case FOUNDED_AT -> orderSpecifiers.addAll(getOrderSpecifier(order, employerProfile.foundedAt));
        case BUSINESS_NUMBER_VERIFIED_AT ->
          orderSpecifiers.addAll(getOrderSpecifier(order, businessRegistration.verifiedAt));
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
