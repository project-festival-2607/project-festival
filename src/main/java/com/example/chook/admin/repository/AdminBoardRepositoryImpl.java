package com.example.chook.admin.repository;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.dto.board.AdminInquiryTableDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.example.chook.member.entity.QMember.member;
import static com.example.chook.support.entity.QInquiry.inquiry;

@Repository
@Slf4j
public class AdminBoardRepositoryImpl implements AdminBoardRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public AdminBoardRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<AdminInquiryTableDTO> getInquiryListPage(Pageable pageable, InquirySearchCondition condition) {
    return null;
  }
}
