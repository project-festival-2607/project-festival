package com.example.chook.admin.service;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.repository.AdminEventRepository;
import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {

  private final AdminEventRepository adminEventRepository;
  private final FestivalRepository festivalRepository;
  private final MemberRepository memberRepository;

  @Override
  public Page<FestivalTableDTO> getFestivalPage(int pageIdx, int pageSize, FestivalSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminEventRepository.getFestivalPage(pageable, condition);
  }

  @Transactional
  @Override
  public boolean assignFestivalMember(String contentId, Long memberId) {
    Festival festival = festivalRepository.findById(contentId).orElseThrow(EntityNotFoundException::new);
    Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);

    if (festival.getMember() == member) return false;
    festival.setMember(member);
    return true;
  }

  @Transactional
  @Override
  public boolean unassignFestivalMember(String contentId) {
    Festival festival = festivalRepository.findById(contentId).orElseThrow(EntityNotFoundException::new);
    if (festival.getMember() == null) return false;
    festival.setMember(null);
    return true;
  }

}
