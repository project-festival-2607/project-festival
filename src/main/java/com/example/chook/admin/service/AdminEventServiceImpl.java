package com.example.chook.admin.service;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.repository.AdminEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {

  private final AdminEventRepository adminEventRepository;

  @Override
  public Page<FestivalTableDTO> getFestivalPage(int pageIdx, int pageSize, FestivalSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminEventRepository.getFestivalPage(pageable, condition);
  }

}
