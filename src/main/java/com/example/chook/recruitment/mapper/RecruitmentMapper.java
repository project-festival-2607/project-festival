package com.example.chook.recruitment.mapper;

import com.example.chook.festival.Festival;
import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.form.RecruitmentManagementForm;
import com.example.chook.recruitment.form.RecruitmentSearchForm;
import com.example.chook.recruitment.record.RecruitmentManagementCondition;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.example.chook.region.entity.RegionSigungu;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RecruitmentMapper {

  // DTO → Entity 변환

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
      .recruitment(recruitment)
      .wageType(dto.getWageType())
      .wageValue(dto.getWageValue())
      .build();
  }

  public RecruitmentFoodTruck toFoodTruckEntity(Recruitment recruitment,
                                                RecruitmentFoodTruckDTO dto) {
    return RecruitmentFoodTruck.builder()
      .recruitment(recruitment)
      .prepaid(dto.isPrepaid())
      .boothFeeRequired(dto.isBoothFeeRequired())
      .electricityProvided(dto.isElectricityProvided())
      .build();
  }

  public void updateEntity(Recruitment recruitment,
                           RecruitmentUpdateDTO dto,
                           RegionSigungu sigungu) {
    recruitment.setSigungu(sigungu);
    recruitment.setWorkingLocation(dto.getWorkingLocation());
    recruitment.setTitle(dto.getRecruitmentTitle());
    recruitment.setContent(dto.getContent());
    recruitment.setApplicationDeadline(dto.getApplicationDeadline());
    recruitment.setRecruitmentCount(dto.getRecruitmentCount());
    recruitment.setWorkingStartDate(dto.getWorkingStartDate());
    recruitment.setWorkingEndDate(dto.getWorkingEndDate());
    recruitment.setWorkingStartTime(dto.getWorkingStartTime());
    recruitment.setWorkingEndTime(dto.getWorkingEndTime());
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

  // Entity → DTO 변환

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

  public RecruitmentResponseDTO toResponseDto(Recruitment entity, String organizerPhone) {
    return toResponseDtoBuilder(entity, organizerPhone).build();
  }

  public RecruitmentResponseDTO toResponseDto(Recruitment entity,
                                              RecruitmentIndividual individual,
                                              String organizerPhone) {
    return toResponseDtoBuilder(entity, organizerPhone)
      .specific(toSpecificDto(individual))
      .build();
  }

  public RecruitmentResponseDTO toResponseDto(Recruitment entity,
                                              RecruitmentFoodTruck foodTruck,
                                              String organizerPhone) {
    return toResponseDtoBuilder(entity, organizerPhone)
      .specific(toSpecificDto(foodTruck))
      .build();
  }

  // Form →DTO/Condition

  public RecruitmentCreateDTO toCreateDto(RecruitmentCreateForm form) {
    return RecruitmentCreateDTO.builder()
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .workingLocation(form.workingLocation())
      .festivalContentId(form.festivalContentId())
      .category(form.category())
      .recruitmentTitle(form.recruitmentTitle())
      .content(form.content())
      .specific(toSpecificDto(form))
      .applicationDeadline(form.applicationDeadline())
      .recruitmentCount(form.recruitmentCount())
      .workingStartDate(form.workingStartDate())
      .workingEndDate(form.workingEndDate())
      .workingStartTime(form.workingStartTime())
      .workingEndTime(form.workingEndTime())
      .build();
  }

  public RecruitmentSearchCondition toCondition(RecruitmentSearchForm form) {
    return RecruitmentSearchCondition.builder()
      .keywordList(splitByRegex(form.keywords(), "[\\s,&]+"))
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .category(form.category())
      .workingStartTime(form.workingStartTime())
      .workingEndTime(form.workingEndTime())
      .workingStartDate(form.workingStartDate())
      .workingEndDate(form.workingEndDate())
      .listCriteria(form.listCriteria())
      .wageType(form.wageType())
      .boothFeeRequired(form.boothFeeRequired())
      .electricityProvided(form.electricityProvided())
      .prepaid(form.prepaid())
      .build();
  }

  public RecruitmentManagementCondition toCondition(RecruitmentManagementForm form, String userName) {
    return RecruitmentManagementCondition.builder()
      .festivalContentId(form.festivalContentId())
      .festivalUserName(userName)
      .category(form.category())
      .status(form.status())
      .isPublished(form.isPublished())
      .isDeleted(form.isDeleted())
      .listCriteria(form.listCriteria())
      .build();
  }


  // Entity → Recruitment*DTO.Recruitment*DTOBuilder

  private RecruitmentUpdateDTO.RecruitmentUpdateDTOBuilder toUpdateDtoBuilder(Recruitment entity) {
    return RecruitmentUpdateDTO.builder()
      .regionSidoCode(entity.getSigungu().getSido().getCode())
      .regionSigunguCode(entity.getSigungu().getCode())
      .workingLocation(entity.getWorkingLocation())
      .recruitmentTitle(entity.getTitle())
      .content(entity.getContent())
      .applicationDeadline(entity.getApplicationDeadline())
      .recruitmentCount(entity.getRecruitmentCount())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      ;
  }

  private RecruitmentResponseDTO.RecruitmentResponseDTOBuilder toResponseDtoBuilder(Recruitment entity,
                                                                                    String organizerPhone) {
    return RecruitmentResponseDTO.builder()
      .recruitmentId(entity.getId())
      .regionSidoName(entity.getSigungu().getSido().getName())
      .regionSigunguName(entity.getSigungu().getName())
      .workingLocation(entity.getWorkingLocation())
      .festivalContentId(entity.getFestival().getContentId())
      .festivalTitle(entity.getFestival().getTitle())
      .category(entity.getCategory())
      .recruitmentTitle(entity.getTitle())
      .content(entity.getContent())
      .applicationDeadline(entity.getApplicationDeadline())
      .recruitmentCount(entity.getRecruitmentCount())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      .status(entity.getStatus())
      .publishedAt(entity.getPublishedAt())
      .updatedAt(entity.getPublishedAt())
      .deletedAt(entity.getDeletedAt())
      .organizerPhone(organizerPhone)
      ;
  }

  private RecruitmentManagementListDTO.RecruitmentManagementListDTOBuilder toManagementListDtoBuilder(Recruitment entity) {
    return RecruitmentManagementListDTO.builder()
      .recruitmentId(entity.getId())
      .regionSidoName(entity.getSigungu().getSido().getShortName())
      .regionSigunguName(entity.getSigungu().getName())
      .festivalContentId(entity.getFestival().getContentId())
      .festivalTitle(entity.getFestival().getTitle())
      .category(entity.getCategory())
      .recruitmentTitle(entity.getTitle())
      .applicationDeadline(entity.getApplicationDeadline())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      .status(entity.getStatus())
      .publishedAt(entity.getPublishedAt())
      .updatedAt(entity.getUpdatedAt())
      .deletedAt(entity.getDeletedAt())
      ;
  }

  private RecruitmentListDTO.RecruitmentListDTOBuilder toListDtoBuilder(Recruitment entity) {
    return RecruitmentListDTO.builder()
      .recruitmentId(entity.getId())
      .regionSidoName(entity.getSigungu().getSido().getShortName())
      .regionSigunguName(entity.getSigungu().getName())
      .festivalContentId(entity.getFestival().getContentId())
      .festivalTitle(entity.getFestival().getTitle())
      .category(entity.getCategory())
      .recruitmentTitle(entity.getTitle())
      .applicationDeadline(entity.getApplicationDeadline())
      .workingStartDate(entity.getWorkingStartDate())
      .workingEndDate(entity.getWorkingEndDate())
      .workingStartTime(entity.getWorkingStartTime())
      .workingEndTime(entity.getWorkingEndTime())
      ;
  }

  private RecruitmentIndividualDTO toSpecificDto(RecruitmentIndividual entity) {
    return RecruitmentIndividualDTO.builder()
      .wageType(entity.getWageType())
      .wageValue(entity.getWageValue())
      .build();
  }

  private RecruitmentFoodTruckDTO toSpecificDto(RecruitmentFoodTruck entity) {
    return RecruitmentFoodTruckDTO.builder()
      .prepaid(entity.isPrepaid())
      .boothFeeRequired(entity.isBoothFeeRequired())
      .electricityProvided(entity.isElectricityProvided())
      .build();
  }

  // Form → SpecificDTO

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
    if (keywords == null || keywords.isBlank()) return List.of();
    return Arrays.stream(keywords.trim().split(regex)).toList();
  }

}
