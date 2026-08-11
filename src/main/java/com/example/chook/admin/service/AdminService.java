package com.example.chook.admin.service;

import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.dto.JobSeekerListDTO;
import org.springframework.data.domain.Page;

public interface AdminService {

  Page<JobSeekerListDTO> getPage(int pageIdx, JobSeekerSearchCondition condition);

}
