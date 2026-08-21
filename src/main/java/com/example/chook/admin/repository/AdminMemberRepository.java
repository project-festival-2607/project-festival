package com.example.chook.admin.repository;

import com.example.chook.admin.condition.member.JobEquipSearchCondition;
import com.example.chook.admin.condition.member.JobSeekerSearchCondition;
import com.example.chook.admin.condition.member.RecruiterSearchCondition;
import com.example.chook.admin.dto.member.JobEquipTableDTO;
import com.example.chook.admin.dto.member.JobSeekerTableDTO;
import com.example.chook.admin.dto.member.RecruiterTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminMemberRepository {

  Page<JobSeekerTableDTO> getJobSeekerPage(Pageable pageable, JobSeekerSearchCondition condition);

  Page<JobEquipTableDTO> getJobEquipPage(Pageable pageable, JobEquipSearchCondition condition);

  Page<RecruiterTableDTO> getRecruiterPage(Pageable pageable, RecruiterSearchCondition condition);

}
