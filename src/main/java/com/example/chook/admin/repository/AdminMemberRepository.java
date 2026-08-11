package com.example.chook.admin.repository;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import org.springframework.data.domain.Page;

public interface AdminMemberRepository {

  Page<JobSeekerTableDTO> getPage(int pageIdx, JobSeekerSearchCondition condition);

}
