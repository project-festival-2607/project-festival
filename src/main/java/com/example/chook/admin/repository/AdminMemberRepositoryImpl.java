package com.example.chook.admin.repository;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.entity.enums.JobSeekerDateRangeCriteria;
import com.example.chook.admin.entity.enums.JobSeekerKeywordType;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.member.entity.enums.Provider;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.example.chook.common.util.QuerydslUtils.*;
import static com.example.chook.member.entity.QJobSeekerProfile.jobSeekerProfile;
import static com.example.chook.member.entity.QMember.member;

@Repository
@Slf4j
public class AdminMemberRepositoryImpl implements AdminMemberRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminMemberRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }


  @Override
  public Page<JobSeekerTableDTO> getPage(int pageIdx, JobSeekerSearchCondition condition) {

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
        jobSeekerProfile.detailAddress

      ))
      .from(member)
      .join(jobSeekerProfile)
      .on(jobSeekerProfile.memberId.eq(member.id));

    JPAQuery<Long> countQuery = jpaQueryFactory
      .select(member.count())
      .from(member);

    BooleanBuilder whereCondition = new BooleanBuilder();

    whereCondition
      .and(containsAnyKeywordWithCriteria(condition.keywordList(), condition.keywordType()))
      .and(eq(member.status, condition.status()))
      .and(eq(jobSeekerProfile.gender, condition.gender()))
      .and(checkDataRangeCriteria(condition.dateRangeCriteria(), condition.startDateTime(), condition.endDateTime()))
      .and(containsProvider(condition.provider()));


    return null;

  }

  private BooleanExpression containsProvider(Provider provider) {
    return member.socialLogins.any().provider.eq(provider);
  }

  private BooleanBuilder checkDataRangeCriteria(JobSeekerDateRangeCriteria criteria,
                                                LocalDateTime startDateTime,
                                                LocalDateTime endDateTime) {

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
        result.and(goe(jobSeekerProfile.birthDate, startDateTime.toLocalDate()));
        result.and(lt(jobSeekerProfile.birthDate, endDateTime.toLocalDate().plusDays(1)));
      }
    }

    return result;
  }

  private BooleanBuilder containsAnyKeywordWithCriteria(List<String> keywordList,
                                                        JobSeekerKeywordType keywordType) {

    if (keywordList == null || keywordList.isEmpty()) return null;
    List<JobSeekerKeywordType> types =
      keywordType == null
        ? Arrays.asList(JobSeekerKeywordType.values())
        : List.of(keywordType);

    BooleanBuilder result = new BooleanBuilder();

    for (String keyword : keywordList) {
      BooleanBuilder keywordResult = new BooleanBuilder();
      for (JobSeekerKeywordType type : types) {
        switch (type) {
          case USERNAME -> {
            keywordResult.or(eq(member.username, keyword));
          }
          case NAME -> {
            keywordResult.or(eq(member.name, keyword));
          }
          case PHONE -> {
            keywordResult.or(eq(member.phone, keyword));
          }
          case EMAIL -> {
            keywordResult.or(eq(member.email, keyword));
          }
          case ADDRESS -> {
            keywordResult.or(eq(jobSeekerProfile.streetAddress, keyword));
            keywordResult.or(eq(jobSeekerProfile.detailAddress, keyword));
          }
        }
      }
      result.and(keywordResult);
    }
    return result;
  }
}
