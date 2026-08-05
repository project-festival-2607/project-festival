package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepositoryCustom {

    List<Recruitment> searchRecruitments(RecruitmentSearchCondition condition);

    Optional<RecruitmentIndividual> getIndividualById(Long id);
    Optional<RecruitmentFoodTruck> getFoodTruckById(Long id);

}
