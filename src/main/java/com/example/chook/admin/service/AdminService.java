package com.example.chook.admin.service;

import com.example.chook.admin.condition.JobSeekerSearchCondition;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import org.springframework.data.domain.Page;

public interface AdminService {

  Page<JobSeekerTableDTO> getJobSeekerPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition);

  boolean suspendMember(Long memberId, String reason);
  boolean unsuspendMember(Long memberId);

  void removePhoneVerification(Long memberId);
  boolean addPhoneWithVerification(Long memberId, String phone);

  void removeBusinessRegistration(Long memberId);
  boolean addBusinessRegistration(Long memberId, String businessNumber);
}
