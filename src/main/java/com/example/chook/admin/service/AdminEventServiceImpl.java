package com.example.chook.admin.service;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.condition.event.RecruitmentSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.dto.event.RecruitmentTableDTO;
import com.example.chook.admin.dto.event.RegionOption;
import com.example.chook.admin.mapper.AdminMapper;
import com.example.chook.admin.repository.AdminEventRepository;
import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.region.repository.RegionSidoRepository;
import com.example.chook.region.repository.RegionSigunguRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {

  private final AdminMapper adminMapper;
  private final AdminEventRepository adminEventRepository;
  private final FestivalRepository festivalRepository;
  private final MemberRepository memberRepository;
  private final RegionSidoRepository regionSidoRepository;
  private final RegionSigunguRepository regionSigunguRepository;

  @Override
  public Page<FestivalTableDTO> getFestivalPage(int pageIdx, int pageSize, FestivalSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminEventRepository.getFestivalPage(pageable, condition);
  }

  @Override
  public Page<RecruitmentTableDTO> getRecruitmentPage(int pageIdx, int pageSize, RecruitmentSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminEventRepository.getRecruitmentPage(pageable, condition);
  }

  @Override
  public List<RegionOption> getSidoOptionList() {
    return regionSidoRepository.findAll().stream().map(
      adminMapper::toOption
    ).toList();
  }

  @Override
  public List<RegionOption> getSigunguOptionListFromSidoCode(String sidoCode) {
    return regionSigunguRepository.findBySido_Code(
      sidoCode,
      Sort.by(Sort.Direction.ASC, "name")
    ).stream().map(
      adminMapper::toOption
    ).toList();
  }

  @Transactional
  @Override
  public boolean assignFestivalMember(String contentId, Long memberId) {
    Festival festival = festivalRepository.findById(contentId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 행사입니다."));
    Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

    if (festival.getMember() == member) return false;
    festival.setMember(member);
    return true;
  }

  @Transactional
  @Override
  public boolean unassignFestivalMember(String contentId) {
    Festival festival = festivalRepository.findById(contentId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 행사입니다."));
    if (festival.getMember() == null) return false;
    festival.setMember(null);
    return true;
  }

}
