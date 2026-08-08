package com.example.chook.recruitment.repository;

import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.form.RecruitmentManagementForm;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RecruitmentRepositoryCustom {

    Page<Recruitment> searchRecruitments(RecruitmentSearchCondition condition, Pageable pageable);

    Optional<RecruitmentIndividual> getIndividualById(Long id);
    Optional<RecruitmentFoodTruck> getFoodTruckById(Long id);

    Page<Recruitment> searchRecruitments(RecruitmentManagementForm form, Pageable pageable);
}
