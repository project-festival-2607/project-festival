package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;

import java.util.List;

public interface RecruitmentRepositoryCustom {

    List<Recruitment> searchRecruitments(RecruitmentSearchCondition condition);

}
