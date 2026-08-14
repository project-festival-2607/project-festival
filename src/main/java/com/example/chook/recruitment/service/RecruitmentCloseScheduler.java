package com.example.chook.recruitment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentCloseScheduler {

  private final RecruitmentService recruitmentService;

  // 모집 날짜가 지난 RECRUITING/PAUSED 공고를 CLOSED로 전환 (FileSweeper와 동일한 스케줄링 패턴)
  @Scheduled(cron = "${recruitment.close-expired.cron}")
  public void closeExpiredRecruitments() {
    log.info("마감일이 지난 모집공고 정리 시작");
    recruitmentService.closeExpiredRecruitments();
    log.info("마감일이 지난 모집공고 정리 종료");
  }

}