package com.example.chook.dev;

import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class DevFestivalDataInitializer {

  private final FestivalRepository festivalRepository;
  private final MemberRepository memberRepository;

  private final Random random = new Random();

  @Transactional
  public void generateSampleFestivals(long targetFestivalCount) {

    log.info("테스트용 행사 데이터 삽입 시작");

    long festivalCountToAdd = Math.max(targetFestivalCount - festivalRepository.count(), 0);
    if (festivalCountToAdd == 0) return;

    List<Member> recruiters = memberRepository.findByRole(MemberRole.RECRUITER);
    long earliestStartDateEpochDay = LocalDate.now().minusDays(7).toEpochDay();
    long latestStartDateEpochDay = LocalDate.now().plusMonths(3).toEpochDay();
    int maxFestivalDuration = 7;

    for (long i = 1; i <= festivalCountToAdd; i++) {

      LocalDate startDate = LocalDate.ofEpochDay(random.nextLong(
        earliestStartDateEpochDay,
        latestStartDateEpochDay + 1
      ));
      LocalDate endDate = startDate.plusDays(random.nextInt(maxFestivalDuration) + 1);
      festivalRepository.save(
        Festival.builder()
          .contentId(UUID.randomUUID().toString())
          .title(String.format("예시 행사 #%02d", i))
          .startDate(startDate)
          .endDate(endDate)
          .member(recruiters.get(random.nextInt(recruiters.size())))
          .firstImage(String.format(
            "/images/dev/festival/%d.png",
            ((i - 1) % 3) + 1
          ))
          .build()
      );
    }

    log.info("테스트용 행사 데이터 삽입 완료");

  }
}
