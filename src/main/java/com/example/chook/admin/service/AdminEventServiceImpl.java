package com.example.chook.admin.service;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {



  @Override
  public Page<FestivalTableDTO> getFestivalPage(int pageIdx, int pageSize, FestivalSearchCondition condition) {
    return null;
  }

}
