package com.example.chook.admin.service;

import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.repository.AdminMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

  private final AdminMemberRepository adminMemberRepository;


  @Override
  public Page<JobSeekerTableDTO> getPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminMemberRepository.getPage(pageable, condition);
  }
}
