package com.example.chook.admin.service;

import com.example.chook.admin.condition.JobEquipSearchCondition;
import com.example.chook.admin.condition.JobSeekerSearchCondition;
import com.example.chook.admin.condition.RecruiterSearchCondition;
import com.example.chook.admin.dto.JobEquipTableDTO;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.dto.RecruiterTableDTO;
import org.springframework.data.domain.Page;

public interface AdminMemberService {

  Page<JobSeekerTableDTO> getJobSeekerPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition);

  Page<JobEquipTableDTO> getJobEquipPage(int pageIdx, int pageSize, JobEquipSearchCondition condition);

  Page<RecruiterTableDTO> getRecruiterPage(int pageIdx, int pageSize, RecruiterSearchCondition condition);

  boolean suspendMember(Long memberId, String reason);

  boolean unsuspendMember(Long memberId);

  void removePhoneVerification(Long memberId);

  boolean addPhoneWithVerification(Long memberId, String phone);

  boolean removeBusinessRegistration(Long memberId);

  boolean addBusinessRegistration(Long memberId, String businessNumber);

}
