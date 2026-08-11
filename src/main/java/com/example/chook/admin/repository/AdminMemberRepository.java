package com.example.chook.admin.repository;

import com.example.chook.admin.dto.JobSeekerListDTO;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.member.entity.Member;
import org.springframework.data.domain.Page;

public interface AdminMemberRepository {

  Page<JobSeekerListDTO> getPage(int pageIdx, JobSeekerSearchCondition condition);

}
