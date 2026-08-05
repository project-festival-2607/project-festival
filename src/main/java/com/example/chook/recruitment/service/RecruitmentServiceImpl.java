package com.example.chook.recruitment.service;

import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFile;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.mapper.RecruitmentMapper;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.example.chook.recruitment.repository.RecruitmentFileRepository;
import com.example.chook.recruitment.repository.RecruitmentFoodTruckRepository;
import com.example.chook.recruitment.repository.RecruitmentIndividualRepository;
import com.example.chook.recruitment.repository.RecruitmentRepository;
import com.example.chook.region.entity.RegionSigungu;
import com.example.chook.region.repository.RegionSidoRepository;
import com.example.chook.region.repository.RegionSigunguRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecruitmentServiceImpl implements RecruitmentService {

  private final RecruitmentRepository recruitmentRepository;
  private final RecruitmentIndividualRepository recruitmentIndividualRepository;
  private final RecruitmentFoodTruckRepository recruitmentFoodTruckRepository;
  private final RecruitmentFileRepository recruitmentFileRepository;

  private final RegionSidoRepository regionSidoRepository;
  private final RegionSigunguRepository regionSigunguRepository;
  private final FestivalRepository festivalRepository;

  private static final int PAGE_SIZE = 10;

  private final RecruitmentMapper mapper;

  @Transactional
  @Override
  public Long createRecruitment(RecruitmentCreateDTO dto) {

    String sigunguCode = dto.getRegionSigunguCode();
    String festivalContentId = dto.getFestivalContentId();
    RecruitmentSpecificDTO specificDto = dto.getSpecific();

    RegionSigungu sigungu = regionSigunguRepository
      .findById(sigunguCode)
      .orElseThrow(() -> new EntityNotFoundException(String.format("코드가 \"%s\"인 시군구가 없음", sigunguCode)));

    Festival festival = festivalRepository
      .findById(festivalContentId)
      .orElseThrow(() -> new EntityNotFoundException(String.format("content_id가 \"%s\"인 행사가 없음", festivalContentId)));

    Recruitment recruitment = mapper.toEntity(dto, sigungu, festival);
    recruitmentRepository.save(recruitment);

    switch (dto.getCategory()) {
      case INDIVIDUAL -> recruitmentIndividualRepository.save(
        mapper.toIndividualEntity(recruitment, (RecruitmentIndividualDTO) specificDto
        ));
      case FOOD_TRUCK -> recruitmentFoodTruckRepository.save(
        mapper.toFoodTruckEntity(recruitment, (RecruitmentFoodTruckDTO) specificDto
        ));
    }

    return recruitment.getId();
  }

  @Override
  public RecruitmentUpdateDTO getRecruitmentForUpdate(Long id) {

    Recruitment recruitment = recruitmentRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고가 없음", id)));

    switch (recruitment.getCategory()) {

      case INDIVIDUAL -> {
        RecruitmentIndividual individual = recruitmentIndividualRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고에 Individual 정보가 없음", id)));
        return mapper.toUpdateDto(recruitment, individual);
      }

      case FOOD_TRUCK -> {
        RecruitmentFoodTruck foodTruck = recruitmentFoodTruckRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고에 FoodTruck 정보가 없음", id)));
        return mapper.toUpdateDto(recruitment, foodTruck);
      }

    }
    return mapper.toUpdateDto(recruitment);

  }

  @Override
  public void updateRecruitment(Long id, RecruitmentUpdateDTO dto) {

  }

  @Transactional
  @Override
  public void deleteRecruitment(Long id) {

    // Specific Entity 삭제
    recruitmentFoodTruckRepository.findById(id).ifPresent(recruitmentFoodTruckRepository::delete);
    recruitmentIndividualRepository.findById(id).ifPresent(recruitmentIndividualRepository::delete);

    // RecruitmentFile 연결 삭제, 실제 파일은 sweepUnreferencedFiles가 담당
    List<RecruitmentFile> fileList = recruitmentFileRepository.findByRecruitment_Id(id);
    recruitmentFileRepository.deleteAll(fileList);

    // 본 엔티티 삭제
    recruitmentRepository.findById(id).ifPresent(recruitmentRepository::delete);

  }

  @Override
  public RecruitmentResponseDTO getRecruitment(Long id) {

    Recruitment recruitment = recruitmentRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고가 없음", id)));

    switch (recruitment.getCategory()) {

      case INDIVIDUAL -> {
        RecruitmentIndividual individual = recruitmentIndividualRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고에 Individual 정보가 없음", id)));
        return mapper.toResponseDto(recruitment, individual);
      }

      case FOOD_TRUCK -> {
        RecruitmentFoodTruck foodTruck = recruitmentFoodTruckRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고에 FoodTruck 정보가 없음", id)));
        return mapper.toResponseDto(recruitment, foodTruck);
      }

    }
    return mapper.toResponseDto(recruitment);
  }

  @Override
  public Page<RecruitmentListDTO> getPages(int page, RecruitmentSearchCondition condition) {
    return null;
  }

  @Override
  public Page<RecruitmentManagementListDTO> getManagementPages(int page, RecruitmentSearchCondition condition) {
    return null;
  }
}
