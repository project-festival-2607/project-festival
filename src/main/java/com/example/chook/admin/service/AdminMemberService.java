package com.example.chook.admin.service;

import com.example.chook.admin.condition.member.JobEquipSearchCondition;
import com.example.chook.admin.condition.member.JobSeekerSearchCondition;
import com.example.chook.admin.condition.member.RecruiterSearchCondition;
import com.example.chook.admin.dto.member.JobEquipTableDTO;
import com.example.chook.admin.dto.member.JobSeekerTableDTO;
import com.example.chook.admin.dto.member.RecruiterTableDTO;
import org.springframework.data.domain.Page;

public interface AdminMemberService {

  // 목록 페이지 조회용
  Page<JobSeekerTableDTO> getJobSeekerPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition);

  Page<JobEquipTableDTO> getJobEquipPage(int pageIdx, int pageSize, JobEquipSearchCondition condition);

  Page<RecruiterTableDTO> getRecruiterPage(int pageIdx, int pageSize, RecruiterSearchCondition condition);


  // GET MAPPING

  RecruiterTableDTO getRecruiterDto(Long recruiterId);


  // POST MAPPING

  boolean suspendMember(Long memberId, String reason);

  boolean unsuspendMember(Long memberId);

  void removePhoneVerification(Long memberId);

  boolean addPhoneWithVerification(Long memberId, String phone);

  boolean removeBusinessRegistration(Long memberId);

  boolean addBusinessRegistration(Long memberId, String businessNumber);

}
