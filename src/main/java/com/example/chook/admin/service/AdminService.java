package com.example.chook.admin.service;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.member.entity.enums.MemberStatus;
import org.springframework.data.domain.Page;

public interface AdminService {

  Page<JobSeekerTableDTO> getPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition);

  void removePhoneVerification(Long memberId);

  boolean suspendMember(Long memberId, String reason);

  boolean unsuspendMember(Long memberId);
}
