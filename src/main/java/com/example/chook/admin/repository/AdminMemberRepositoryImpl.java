package com.example.chook.admin.repository;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.entity.enums.JobSeekerDateCriteria;
import com.example.chook.admin.entity.enums.JobSeekerKeywordType;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.Provider;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import java.util.List;

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
  public Page<JobSeekerTableDTO> getPage(Pageable pageable, JobSeekerSearchCondition condition) {

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
      .and(containsAnyKeywordWithCriteria(condition.keywordList(), condition.keywordType()))
      .and(eq(jobSeekerProfile.gender, condition.gender()))
      .and(checkDataRangeCriteria(condition.dateRangeCriteria(),
        condition.startDateTime(),
        condition.endDateTime(),
        condition.startDate(),
        condition.endDate()
      ))
      .and(containsProvider(condition.provider()));

    List<JobSeekerTableDTO> content = resultQuery
      .where(whereCondition)
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
      case BIRTH_DATE -> {
        result.and(goe(jobSeekerProfile.birthDate, startDate));
        result.and(lt(jobSeekerProfile.birthDate, endDate));
      }
      // 이 경우에 속하지 않는 경우 날짜 기준 필터를 사용하지 않음
    }

    return result;
  }

  private BooleanBuilder containsAnyKeywordWithCriteria(List<String> keywordList,
                                                        JobSeekerKeywordType keywordType) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<JobSeekerKeywordType> types =
      keywordType == null
        ? List.of(JobSeekerKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (JobSeekerKeywordType type : types) {
        switch (type) {
          case USERNAME -> {
            keywordResult.or(contains(member.username, keyword));
          }
          case NAME -> {
            keywordResult.or(contains(member.name, keyword));
          }
          case PHONE -> {
            keywordResult.or(contains(member.phone, keyword));
          }
          case EMAIL -> {
            keywordResult.or(contains(member.email, keyword));
          }
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
}
