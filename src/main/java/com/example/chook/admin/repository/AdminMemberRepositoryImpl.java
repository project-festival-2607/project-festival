package com.example.chook.admin.repository;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AdminMemberRepositoryImpl implements AdminMemberRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminMemberRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }


  @Override
    return null;
  public Page<JobSeekerTableDTO> getPage(int pageIdx, JobSeekerSearchCondition condition) {
  }
}
