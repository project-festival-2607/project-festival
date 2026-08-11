package com.example.chook.admin.service;

import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import org.springframework.data.domain.Page;

public interface AdminService {

  Page<JobSeekerTableDTO> getPage(int pageIdx, JobSeekerSearchCondition condition);

}
