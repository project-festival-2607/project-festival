package com.example.chook.admin.service;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import org.springframework.data.domain.Page;

public interface AdminEventService {

  Page<FestivalTableDTO> getFestivalPage(int pageIdx, int pageSize, FestivalSearchCondition condition);

  boolean assignFestivalMember(String contentId, Long memberId);

  boolean unassignFestivalMember(String contentId);

}
