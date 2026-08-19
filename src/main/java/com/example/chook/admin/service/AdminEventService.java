package com.example.chook.admin.service;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.condition.event.RecruitmentSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import com.example.chook.admin.dto.event.RecruitmentTableDTO;
import com.example.chook.admin.dto.event.RegionOption;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminEventService {

  Page<FestivalTableDTO> getFestivalPage(int pageIdx, int pageSize, FestivalSearchCondition condition);

  Page<RecruitmentTableDTO> getRecruitmentPage(int pageIdx, int pageSize, RecruitmentSearchCondition condition);

  List<RegionOption> getSidoOptionList();

  List<RegionOption> getSigunguOptionListFromSidoCode(String sidoCode);

  boolean assignFestivalMember(String contentId, Long memberId);

  boolean unassignFestivalMember(String contentId);

}
