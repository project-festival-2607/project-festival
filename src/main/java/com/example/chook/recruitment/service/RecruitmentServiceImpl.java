package com.example.chook.recruitment.service;

import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
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
import com.example.chook.region.repository.RegionSigunguRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecruitmentServiceImpl implements RecruitmentService {

  private static final int PAGE_SIZE = 10;

  // 본문 마크다운 안에 박혀있는 "/recruitment/image/{uuid}" 링크로 첨부 이미지를 역추적 (admin_board와 동일한 패턴)
  private static final Pattern IMAGE_UUID_PATTERN =
    Pattern.compile("/recruitment/image/([0-9a-fA-F\\-]{36})");

  private final RecruitmentRepository recruitmentRepository;
  private final RecruitmentIndividualRepository recruitmentIndividualRepository;
  private final RecruitmentFoodTruckRepository recruitmentFoodTruckRepository;
  private final RecruitmentFileRepository recruitmentFileRepository;
  private final RegionSigunguRepository regionSigunguRepository;
  private final FestivalRepository festivalRepository;
  private final UploadedFileRepository uploadedFileRepository;
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

    validateSpecificDtoAndSave(recruitment, specificDto);

    for (UUID uuid : extractImageUuids(dto.getContent())) {
      UploadedFile uploadedFile = uploadedFileRepository.findById(uuid)
        .orElseThrow(() -> new EntityNotFoundException(
          String.format("본문에 참조된 이미지(%s)를 업로드 기록에서 찾을 수 없음", uuid)
        ));
      recruitmentFileRepository.save(
        RecruitmentFile.builder()
          .recruitment(recruitment)
          .uploadedFile(uploadedFile)
          .build()
      );
    }

    return recruitment.getId();

  }

  private List<UUID> extractImageUuids(String content) {
    if (content == null) return List.of();
    return IMAGE_UUID_PATTERN.matcher(content)
      .results()
      .map(result -> UUID.fromString(result.group(1)))
      .distinct()
      .toList();
  }

  @Override
  public RecruitmentUpdateDTO getRecruitmentForUpdate(Long id) {

    Recruitment recruitment = recruitmentRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고가 없음", id)));

    switch (recruitment.getCategory()) {

      case INDIVIDUAL -> {
        RecruitmentIndividual individual = getIndividual(id);
        return mapper.toUpdateDto(recruitment, individual);
      }

      case FOOD_TRUCK -> {
        RecruitmentFoodTruck foodTruck = getFoodTruck(id);
        return mapper.toUpdateDto(recruitment, foodTruck);
      }

    }
    return mapper.toUpdateDto(recruitment);

  }

  @Transactional
  @Override
  public void updateRecruitment(Long id, RecruitmentUpdateDTO dto) {

    String sigunguCode = dto.getRegionSigunguCode();
    RecruitmentSpecificDTO specificDto = dto.getSpecific();

    RegionSigungu sigungu = regionSigunguRepository
      .findById(sigunguCode)
      .orElseThrow(() -> new EntityNotFoundException(String.format("코드가 \"%s\"인 시군구가 없음", sigunguCode)));

    Recruitment recruitment = recruitmentRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 행사가 없음", id)));

    recruitmentFoodTruckRepository.deleteById(id);
    recruitmentIndividualRepository.deleteById(id);

    validateSpecificDtoAndSave(recruitment, specificDto);

    recruitment.setSigungu(sigungu);
    recruitment.setTitle(dto.getRecruitmentTitle());
    recruitment.setContent(dto.getContent());
    recruitment.setApplicationDeadline(dto.getApplicationDeadline());
    recruitment.setRecruitmentCount(dto.getRecruitmentCount());
    recruitment.setWorkingLocation(dto.getWorkingLocation());
    recruitment.setWorkingStartDate(dto.getWorkingStartDate());
    recruitment.setWorkingEndDate(dto.getWorkingEndDate());
    recruitment.setWorkingStartTime(dto.getWorkingStartTime());
    recruitment.setWorkingEndTime(dto.getWorkingEndTime());

    recruitmentRepository.save(recruitment);

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
        RecruitmentIndividual individual = getIndividual(id);
        return mapper.toResponseDto(recruitment, individual);
      }

      case FOOD_TRUCK -> {
        RecruitmentFoodTruck foodTruck = getFoodTruck(id);
        return mapper.toResponseDto(recruitment, foodTruck);
      }

    }
    return mapper.toResponseDto(recruitment);
  }

  @Override
  public Page<RecruitmentListDTO> getPage(int pageIdx, RecruitmentSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, PAGE_SIZE);
    return recruitmentRepository
      .searchRecruitments(condition, pageable)
      .map(this::buildListDtoFromEntity);
  }

  @Override
  public Page<RecruitmentManagementListDTO> getManagementPage(int pageIdx, RecruitmentSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, PAGE_SIZE);
    return recruitmentRepository
      .searchRecruitments(condition, pageable)
      .map(this::buildManagementListDtoFromEntity);
  }

  private void validateSpecificDtoAndSave(Recruitment recruitment,
                                          RecruitmentSpecificDTO specificDto) {
    switch (recruitment.getCategory()) {
      case INDIVIDUAL -> {
        if (!(specificDto instanceof RecruitmentIndividualDTO individualDto))
          throw new IllegalArgumentException("specific이 Individual이 아님");
        recruitmentIndividualRepository.save(
          mapper.toIndividualEntity(recruitment, individualDto));
      }
      case FOOD_TRUCK -> {
        if (!(specificDto instanceof RecruitmentFoodTruckDTO foodTruckDto))
          throw new IllegalArgumentException("specific이 FoodTruck이 아님");
        recruitmentFoodTruckRepository.save(
          mapper.toFoodTruckEntity(recruitment, foodTruckDto));
      }
    }
  }

  private RecruitmentIndividual getIndividual(Long id) {
    return recruitmentIndividualRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고에 Individual 정보가 없음", id)));
  }

  private RecruitmentFoodTruck getFoodTruck(Long id) {
    return recruitmentFoodTruckRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고에 FoodTruck 정보가 없음", id)));
  }

  private RecruitmentListDTO buildListDtoFromEntity(Recruitment entity) {
    switch (entity.getCategory()) {
      case INDIVIDUAL -> {
        return mapper.toListDto(entity, getIndividual(entity.getId()));
      }
      case FOOD_TRUCK -> {
        return mapper.toListDto(entity, getFoodTruck(entity.getId()));
      }
      default -> {
        return mapper.toListDto(entity);
      }
    }
  }

  private RecruitmentManagementListDTO buildManagementListDtoFromEntity(Recruitment entity) {
    switch (entity.getCategory()) {
      case INDIVIDUAL -> {
        return mapper.toManagementListDto(entity, getIndividual(entity.getId()));
      }
      case FOOD_TRUCK -> {
        return mapper.toManagementListDto(entity, getFoodTruck(entity.getId()));
      }
      default -> {
        return mapper.toManagementListDto(entity);
      }
    }
  }


}
