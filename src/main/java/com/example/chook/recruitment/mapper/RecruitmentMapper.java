package com.example.chook.recruitment.mapper;

import com.example.chook.festival.Festival;
import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.form.RecruitmentManagementSearchForm;
import com.example.chook.recruitment.form.RecruitmentSearchForm;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.example.chook.region.entity.RegionSigungu;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RecruitmentMapper {

  public Recruitment toEntity(RecruitmentCreateDTO dto,
                              RegionSigungu sigungu,
                              Festival festival) {
    return Recruitment.builder()
      .sigungu(sigungu)
      .workingLocation(dto.getWorkingLocation())
      .festival(festival)
      .category(dto.getCategory())
      .title(dto.getRecruitmentTitle())
      .content(dto.getContent())
      .applicationDeadline(dto.getApplicationDeadline())
      .recruitmentCount(dto.getRecruitmentCount())
      .workingStartDate(dto.getWorkingStartDate())
      .workingEndDate(dto.getWorkingEndDate())
      .workingStartTime(dto.getWorkingStartTime())
      .workingEndTime(dto.getWorkingEndTime())
      .build();
  }

  public RecruitmentIndividual toIndividualEntity(Recruitment recruitment,
                                                  RecruitmentIndividualDTO dto) {
    return RecruitmentIndividual.builder()
      .recruit(recruitment)
      .wageType(dto.getWageType())
      .wageValue(dto.getWageValue())
      .build();
  }

  public RecruitmentFoodTruck toFoodTruckEntity(Recruitment recruitment,
                                                RecruitmentFoodTruckDTO dto) {
    return RecruitmentFoodTruck.builder()
      .recruit(recruitment)
      .prepaid(dto.isPrepaid())
      .boothFeeRequired(dto.isBoothFeeRequired())
      .electricityProvided(dto.isElectricityProvided())
      .build();
  }

  public void updateEntity(Recruitment recruitment,
                           RecruitmentUpdateDTO dto,
                           RegionSigungu sigungu) {
    recruitment.setSigungu(sigungu);
    recruitment.setTitle(dto.getRecruitmentTitle());
    recruitment.setContent(dto.getContent());
    recruitment.setApplicationDeadline(dto.getApplicationDeadline());
    recruitment.setRecruitmentCount(dto.getRecruitmentCount());
    recruitment.setWorkingStartDate(dto.getWorkingStartDate());
    recruitment.setWorkingEndDate(dto.getWorkingEndDate());
    recruitment.setWorkingStartTime(dto.getWorkingStartTime());
    recruitment.setWorkingEndTime(dto.getWorkingEndTime());
    recruitment.setWorkingLocation(dto.getWorkingLocation());
  }

  public void updateIndividualEntity(RecruitmentIndividual individual,
                                     RecruitmentIndividualDTO dto) {
    individual.setWageType(dto.getWageType());
    individual.setWageValue(dto.getWageValue());
  }

  public void updateFoodTruckEntity(RecruitmentFoodTruck foodTruck,
                                    RecruitmentFoodTruckDTO dto) {
    foodTruck.setPrepaid(dto.isPrepaid());
    foodTruck.setBoothFeeRequired(dto.isBoothFeeRequired());
    foodTruck.setElectricityProvided(dto.isElectricityProvided());
  }

  public RecruitmentUpdateDTO toUpdateDto(Recruitment entity) {
    return toUpdateDtoBuilder(entity).build();
  }

  public RecruitmentUpdateDTO toUpdateDto(Recruitment entity,
                                          RecruitmentIndividual individual) {
    return toUpdateDtoBuilder(entity)
      .specific(toSpecificDto(individual))
      .build();
  }

  public RecruitmentUpdateDTO toUpdateDto(Recruitment entity,
                                          RecruitmentFoodTruck foodTruck) {
    return toUpdateDtoBuilder(entity)
      .specific(toSpecificDto(foodTruck))
      .build();
  }

  public RecruitmentListDTO toListDto(Recruitment entity) {
    return toListDtoBuilder(entity).build();
  }

  public RecruitmentListDTO toListDto(Recruitment entity,
                                      RecruitmentIndividual individual) {
    return toListDtoBuilder(entity)
      .specific(toSpecificDto(individual))
      .build();
  }

  public RecruitmentListDTO toListDto(Recruitment entity,
                                      RecruitmentFoodTruck foodTruck) {
    return toListDtoBuilder(entity)
      .specific(toSpecificDto(foodTruck))
      .build();
  }

  public RecruitmentManagementListDTO toManagementListDto(Recruitment entity) {
    return toManagementListDtoBuilder(entity).build();
  }

  public RecruitmentManagementListDTO toManagementListDto(Recruitment entity,
                                                          RecruitmentIndividual individual) {
    return toManagementListDtoBuilder(entity)
      .specific(toSpecificDto(individual))
      .build();
  }

  public RecruitmentManagementListDTO toManagementListDto(Recruitment entity,
                                                          RecruitmentFoodTruck foodTruck) {
    return toManagementListDtoBuilder(entity)
      .specific(toSpecificDto(foodTruck))
      .build();
  }

  public RecruitmentResponseDTO toResponseDto(Recruitment entity) {
    return toResponseDtoBuilder(entity).build();
  }

  public RecruitmentResponseDTO toResponseDto(Recruitment entity,
                                              RecruitmentIndividual individual) {
    return toResponseDtoBuilder(entity)
      .specific(toSpecificDto(individual))
      .build();
  }

  public RecruitmentResponseDTO toResponseDto(Recruitment entity,
                                              RecruitmentFoodTruck foodTruck) {
    return toResponseDtoBuilder(entity)
      .specific(toSpecificDto(foodTruck))
      .build();
  }

  public RecruitmentCreateDTO toCreateDto(RecruitmentCreateForm form) {
    return RecruitmentCreateDTO.builder()
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .recruitmentTitle(form.recruitmentTitle())
      .festivalContentId(form.festivalContentId())
      .content(form.content())
      .category(form.category())
      .specific(toSpecificDto(form))
      .applicationDeadline(form.applicationDeadline())
      .recruitmentCount(form.recruitmentCount())
      .workingLocation(form.workingLocation())
      .workingStartDate(form.workingStartDate())
      .workingEndDate(form.workingEndDate())
      .workingStartTime(form.workingStartTime())
      .workingEndTime(form.workingEndTime())
      .build();
  }

  public RecruitmentSearchCondition toCondition(RecruitmentManagementSearchForm form) {
    return RecruitmentSearchCondition.builder()
      .keywordList(splitByRegex(form.keywords(), "[\\s,&]+"))
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .category(form.category())
      .status(form.status())
      .festivalContentId(form.festivalContentId())
      .build();
  }

  public RecruitmentSearchCondition toCondition(RecruitmentSearchForm form) {
    return RecruitmentSearchCondition.builder()
      .keywordList(splitByRegex(form.keywords(), "[\\s,&]+"))
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .category(form.category())
      .status(form.status())
      .workingStartTime(form.workingStartTime())
      .workingEndTime(form.workingEndTime())
      .workingStartDate(form.workingStartDate())
      .workingEndDate(form.workingEndDate())
      .listCriteria(form.listCriteria())
      .build();
  }

  private RecruitmentUpdateDTO.RecruitmentUpdateDTOBuilder toUpdateDtoBuilder(Recruitment entity) {
    return  RecruitmentUpdateDTO.builder()
      .regionSidoCode(entity.getSigungu().getSido().getCode())
      .regionSigunguCode(entity.getSigungu().getCode())
      .recruitmentTitle(entity.getTitle())
      .content(entity.getContent())
      .applicationDeadline(entity.getApplicationDeadline())
      .recruitmentCount(entity.getRecruitmentCount())
      .workingLocation(entity.getWorkingLocation())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      ;

  }

  private RecruitmentResponseDTO.RecruitmentResponseDTOBuilder toResponseDtoBuilder(Recruitment entity) {
    return RecruitmentResponseDTO.builder()
      .regionSidoName(entity.getSigungu().getSido().getName())
      .regionSigunguName(entity.getSigungu().getName())
      .recruitmentId(entity.getId())
      .recruitmentTitle(entity.getTitle())
      .festivalContentId(entity.getFestival().getContentId())
      .festivalTitle(entity.getFestival().getTitle())
      .content(entity.getContent())
      .category(entity.getCategory())
      .applicationDeadline(entity.getApplicationDeadline())
      .recruitmentCount(entity.getRecruitmentCount())
      .status(entity.getStatus())
      .workingLocation(entity.getWorkingLocation())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      .published(entity.isPublished())
      .publishedAt(entity.getPublishedAt())
      ;
  }

  private RecruitmentManagementListDTO.RecruitmentManagementListDTOBuilder toManagementListDtoBuilder(Recruitment entity) {
    return RecruitmentManagementListDTO.builder()
      .regionSidoName(entity.getSigungu().getSido().getShortName())
      .regionSigunguName(entity.getSigungu().getName())
      .recruitmentId(entity.getId())
      .recruitmentTitle(entity.getTitle())
      .festivalContentId(entity.getFestival().getContentId())
      .festivalTitle(entity.getFestival().getTitle())
      .category(entity.getCategory())
      .applicationDeadline(entity.getApplicationDeadline())
      .status(entity.getStatus())
      .workingLocation(entity.getWorkingLocation())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      .publishedAt(entity.getPublishedAt())
      ;
  }

  private RecruitmentListDTO.RecruitmentListDTOBuilder toListDtoBuilder(Recruitment entity) {
    return RecruitmentListDTO.builder()
      .regionSidoName(entity.getSigungu().getSido().getShortName())
      .regionSigunguName(entity.getSigungu().getName())
      .recruitmentId(entity.getId())
      .recruitmentTitle(entity.getTitle())
      .festivalContentId(entity.getFestival().getContentId())
      .festivalTitle(entity.getFestival().getTitle())
      .category(entity.getCategory())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      ;
  }

  private RecruitmentSpecificDTO toSpecificDto(RecruitmentIndividual entity) {
    return RecruitmentIndividualDTO.builder()
      .wageType(entity.getWageType())
      .wageValue(entity.getWageValue())
      .build();
  }

  private RecruitmentSpecificDTO toSpecificDto(RecruitmentFoodTruck entity) {
    return RecruitmentFoodTruckDTO.builder()
      .prepaid(entity.isPrepaid())
      .boothFeeRequired(entity.isBoothFeeRequired())
      .electricityProvided(entity.isElectricityProvided())
      .build();
  }

  private RecruitmentSpecificDTO toSpecificDto(RecruitmentCreateForm form) {
    RecruitmentCategory category = form.category();
    if (category == null) return null;
    switch (category) {
      case INDIVIDUAL -> {
        return RecruitmentIndividualDTO.builder()
          .wageType(form.wageType())
          .wageValue(form.wageValue())
          .build();
      }
      case FOOD_TRUCK -> {
        return RecruitmentFoodTruckDTO.builder()
          .prepaid(form.prepaid())
          .boothFeeRequired(form.boothFeeRequired())
          .electricityProvided(form.electricityProvided())
          .build();
      }
    }
    return null;
  }

  private List<String> splitByRegex(String keywords, String regex) {
    return Arrays.stream(keywords.trim().split(regex)).toList();
  }

}
