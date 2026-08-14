package com.example.chook.recruitment.service;

import com.example.chook.chookMain.ChookRecruitmentDTO;
import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFile;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.entity.enums.RecruitmentStatus;
import com.example.chook.recruitment.mapper.RecruitmentMapper;
import com.example.chook.recruitment.record.RecruitmentManagementCondition;
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
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    syncRecruitmentFiles(recruitment, dto.getContent());

    return recruitment.getId();

  }

  // 본문에 남아있는 이미지 UUID 기준으로 RecruitmentFile 연결을 맞춤 (등록/수정 공통)
  // - 새로 참조된 이미지: RecruitmentFile 생성
  // - 더 이상 본문에 없는 이미지: RecruitmentFile 삭제 (실제 파일은 sweepUnreferencedFiles가 담당)
  private void syncRecruitmentFiles(Recruitment recruitment, String content) {
    List<UUID> currentUuids = extractImageUuids(content);
    List<RecruitmentFile> existingFiles = recruitmentFileRepository.findByRecruitment_Id(recruitment.getId());

    List<RecruitmentFile> noLongerReferenced = existingFiles.stream()
      .filter(file -> !currentUuids.contains(file.getUploadedFile().getUuid()))
      .toList();
    recruitmentFileRepository.deleteAll(noLongerReferenced);

    Set<UUID> alreadyLinkedUuids = existingFiles.stream()
      .map(file -> file.getUploadedFile().getUuid())
      .collect(Collectors.toSet());

    for (UUID uuid : currentUuids) {
      if (alreadyLinkedUuids.contains(uuid)) continue;
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

    updateSpecificEntity(recruitment, specificDto);

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

    syncRecruitmentFiles(recruitment, dto.getContent());

  }

  @Transactional
  @Override
  public void deleteRecruitment(Long id) {

    // RecruitmentFile 연결 삭제, 실제 파일은 sweepUnreferencedFiles가 담당
    List<RecruitmentFile> fileList = recruitmentFileRepository.findByRecruitment_Id(id);
    recruitmentFileRepository.deleteAll(fileList);

    // 본 엔티티 삭제. Individual/FoodTruck은 Recruitment의 cascade=REMOVE, orphanRemoval로 함께 삭제됨
    // (별도 리포지토리로 미리 지우면, OSIV로 같은 세션에 남아있는 Recruitment의 in-memory 참조와 어긋나
    //  TransientPropertyValueException이 발생함)
    recruitmentRepository.findById(id).ifPresent(recruitmentRepository::delete);

  }

  @Transactional
  @Override
  public void publish(Long id) {
    recruitmentRepository.publish(id, RecruitmentStatus.RECRUITING);
  }

  @Override
  public RecruitmentResponseDTO getRecruitment(Long id) {

    Recruitment recruitment = recruitmentRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException(String.format("id가 \"%d\"인 공고가 없음", id)));

    String organizerPhone = recruitment.getFestival().getMember().getPhone();

    switch (recruitment.getCategory()) {

      case INDIVIDUAL -> {
        RecruitmentIndividual individual = getIndividual(id);
        return mapper.toResponseDto(recruitment, individual, organizerPhone);
      }

      case FOOD_TRUCK -> {
        RecruitmentFoodTruck foodTruck = getFoodTruck(id);
        return mapper.toResponseDto(recruitment, foodTruck, organizerPhone);
      }

    }
    return mapper.toResponseDto(recruitment, organizerPhone);
  }

  @Override
  public Page<RecruitmentListDTO> getPage(int pageIdx, RecruitmentSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, PAGE_SIZE);
    return recruitmentRepository
      .searchRecruitments(condition, pageable)
      .map(this::buildListDtoFromEntity);
  }

  @Override
  public Page<RecruitmentManagementListDTO> getPage(int pageIdx, RecruitmentManagementCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, PAGE_SIZE);
    return recruitmentRepository
      .searchRecruitments(condition, pageable)
      .map(this::buildManagementListDtoFromEntity);
  }

  @Override
  public List<ChookRecruitmentDTO> getMainList() {
    return recruitmentRepository.findAll()
            .stream()
            .map((rec) -> new ChookRecruitmentDTO(
                    rec.getId(),
                    rec.getTitle(),
                    rec.getWorkingStartDate(),
                    rec.getWorkingEndDate()
            ))
            .collect(Collectors.toList());
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

  // 수정 시에는 카테고리가 바뀌지 않으므로 specific row는 항상 이미 존재함 -> 삭제 후 재생성하지 않고 그대로 값만 갱신
  // (삭제 후 같은 id로 재생성하면 @MapsId 때문에 같은 트랜잭션 안에서 TransientPropertyValueException 발생)
  private void updateSpecificEntity(Recruitment recruitment, RecruitmentSpecificDTO specificDto) {
    switch (recruitment.getCategory()) {
      case INDIVIDUAL -> {
        if (!(specificDto instanceof RecruitmentIndividualDTO individualDto))
          throw new IllegalArgumentException("specific이 Individual이 아님");
        mapper.updateIndividualEntity(getIndividual(recruitment.getId()), individualDto);
      }
      case FOOD_TRUCK -> {
        if (!(specificDto instanceof RecruitmentFoodTruckDTO foodTruckDto))
          throw new IllegalArgumentException("specific이 FoodTruck이 아님");
        mapper.updateFoodTruckEntity(getFoodTruck(recruitment.getId()), foodTruckDto);
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
