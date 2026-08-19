package com.example.chook.admin.repository;

import com.example.chook.admin.condition.event.FestivalSearchCondition;
import com.example.chook.admin.dto.event.FestivalTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminEventRepository {

  Page<FestivalTableDTO> getFestivalPage(Pageable pageable, FestivalSearchCondition condition);

}
