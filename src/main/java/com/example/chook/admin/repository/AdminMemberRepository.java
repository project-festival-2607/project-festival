package com.example.chook.admin.repository;

import com.example.chook.admin.condition.JobSeekerSearchCondition;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminMemberRepository {

  Page<JobSeekerTableDTO> getJobSeekerPage(Pageable pageable, JobSeekerSearchCondition condition);

}
