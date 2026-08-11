package com.example.chook.recruitment.service;

import com.example.chook.chookMain.ChookRecruitmentDTO;
import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.form.RecruitmentManagementForm;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RecruitmentService {

  Long createRecruitment(RecruitmentCreateDTO dto);

  RecruitmentUpdateDTO getRecruitmentForUpdate(Long id);

  void updateRecruitment(Long id, RecruitmentUpdateDTO dto);

  void deleteRecruitment(Long id);

  RecruitmentResponseDTO getRecruitment(Long id);

  Page<RecruitmentListDTO> getPage(int pageIdx, RecruitmentSearchCondition condition);

  Page<RecruitmentManagementListDTO> getPage(int pageIdx, RecruitmentManagementCondition condition);

    List<ChookRecruitmentDTO> getMainList();
}
