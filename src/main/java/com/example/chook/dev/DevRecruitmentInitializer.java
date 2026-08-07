package com.example.chook.dev;

import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.entity.RecruitmentFoodTruck;
import com.example.chook.recruitment.entity.RecruitmentIndividual;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import com.example.chook.recruitment.repository.RecruitmentFoodTruckRepository;
import com.example.chook.recruitment.repository.RecruitmentIndividualRepository;
import com.example.chook.recruitment.repository.RecruitmentRepository;
import com.example.chook.region.entity.RegionSigungu;
import com.example.chook.region.repository.RegionSigunguRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class DevRecruitmentInitializer {

  private final RecruitmentRepository recruitmentRepository;
  private final RecruitmentIndividualRepository recruitmentIndividualRepository;
  private final RecruitmentFoodTruckRepository recruitmentFoodTruckRepository;

  private final FestivalRepository festivalRepository;
  private final RegionSigunguRepository regionSigunguRepository;

  private final Random random = new Random();
  private List<RegionSigungu> sigunguList;

  @Transactional
  public void generateSampleRecruitments() {

    sigunguList = regionSigunguRepository.findAll();

    for (Festival festival : festivalRepository.findAll()) {

      if (recruitmentRepository.countByFestival_ContentId(festival.getContentId()) > 0) continue;

      int recruitmentIndividualCount = random.nextInt(1, 2 + 1);
      int recruitmentFoodTruckCount = random.nextInt(0, 1 + 1);
      int recruitmentEquipmentCount = random.nextInt(0, 1 + 1);
      int recruitmentEtcCount = random.nextInt(0,
        Math.max(1 - recruitmentFoodTruckCount - recruitmentEquipmentCount, 0) + 1
      );

      for (int i = 1; i <= recruitmentIndividualCount; i++) addRecruitmentIndividual(festival);
      for (int i = 1; i <= recruitmentFoodTruckCount; i++) addRecruitmentFoodTruck(festival);
      for (int i = 1; i <= recruitmentEquipmentCount; i++) addRecruitmentEquipment(festival);
      for (int i = 1; i <= recruitmentEtcCount; i++) addRecruitmentEtc(festival);

    }

  }

  private void addRecruitmentEtc(Festival festival) {
    String recruitmentTitle = "기타 공고";
    Recruitment savedRecruitment = addRecruitment(festival, RecruitmentCategory.ETC, recruitmentTitle);
    savedRecruitment.setTitle(String.format("기타 공고 #%d", savedRecruitment.getId()));
    // 구직자 목록 페이지 검증용
    savedRecruitment.setPublished(true);
    savedRecruitment.setPublishedAt(LocalDateTime.now());
  }

  private void addRecruitmentEquipment(Festival festival) {
    String recruitmentTitle = "장비 공고";
    Recruitment savedRecruitment = addRecruitment(festival, RecruitmentCategory.EQUIPMENT, recruitmentTitle);
    savedRecruitment.setTitle(String.format("장비 공고 #%d", savedRecruitment.getId()));
    // 구직자 목록 페이지 검증용
    savedRecruitment.setPublished(true);
    savedRecruitment.setPublishedAt(LocalDateTime.now());
  }

  private void addRecruitmentFoodTruck(Festival festival) {
    String recruitmentTitle = "푸드트럭 공고";
    Recruitment savedRecruitment = addRecruitment(festival, RecruitmentCategory.FOOD_TRUCK, recruitmentTitle);
    savedRecruitment.setTitle(String.format("푸드트럭 공고 #%d", savedRecruitment.getId()));
    addRecruitmentFoodTruckSpecific(savedRecruitment);
    // 구직자 목록 페이지 검증용
    savedRecruitment.setPublished(true);
    savedRecruitment.setPublishedAt(LocalDateTime.now());
  }

  private void addRecruitmentIndividual(Festival festival) {
    String recruitmentTitle = "일반 구인 공고";
    Recruitment savedRecruitment = addRecruitment(festival, RecruitmentCategory.INDIVIDUAL, recruitmentTitle);
    savedRecruitment.setTitle(String.format("일반 구인 공고 #%d", savedRecruitment.getId()));
    addRecruitmentIndividualSpecific(savedRecruitment);
    // 구직자 목록 페이지 검증용
    savedRecruitment.setPublished(true);
    savedRecruitment.setPublishedAt(LocalDateTime.now());
  }

  private Recruitment addRecruitment(Festival festival, RecruitmentCategory category, String recruitmentTitle) {

    RegionSigungu sigungu = sigunguList.get(random.nextInt(sigunguList.size()));
    int startTime = random.nextInt(47 + 1);   // 30분 단위
    int endTime = Math.min(48, startTime + random.nextInt(16 + 1)) % 48;


    return recruitmentRepository.save(
      Recruitment.builder()
        .sigungu(sigungu)
        .festival(festival)
        .category(category)
        .title(recruitmentTitle)
        .content("테스트 공고입니다.")
        .applicationDeadline(LocalDate.now().plusDays(random.nextInt(-3, 3 + 1)))
        .recruitmentCount(category == RecruitmentCategory.INDIVIDUAL ? random.nextInt(5 + 1) : 1)
        .workingStartTime(LocalTime.of(startTime / 2, 30 * (startTime % 2)))
        .workingEndTime(LocalTime.of(endTime / 2, 30 * (endTime % 2)))
              .workingStartDate((festival.getStartDate() != null ? festival.getStartDate() : LocalDate.now())
                      .minusDays(random.nextInt(-3, 0 + 1)))
              .workingEndDate((festival.getEndDate() != null ? festival.getEndDate() : LocalDate.now())
                      .plusDays(random.nextInt(0, 1 + 1)))
        .build()
    );
  }

  private void addRecruitmentIndividualSpecific(Recruitment recruitment) {
    RecruitmentWageType[] wageTypes = RecruitmentWageType.values();
    recruitmentIndividualRepository.save(
      RecruitmentIndividual.builder()
        .recruit(recruitment)
        .wageType(wageTypes[random.nextInt(wageTypes.length)])
        .wageValue(random.nextInt(3, 30 + 1) * 5000)
        .build()
    );
  }

  private void addRecruitmentFoodTruckSpecific(Recruitment recruitment) {
    boolean prepaid = random.nextBoolean();
    recruitmentFoodTruckRepository.save(
      RecruitmentFoodTruck.builder()
        .recruit(recruitment)
        .prepaid(prepaid)
        .boothFeeRequired(random.nextBoolean() && !prepaid)
        .electricityProvided(random.nextBoolean() && !prepaid)
        .build()
    );
  }
}
