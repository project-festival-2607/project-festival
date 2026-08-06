package com.example.chook.dev;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevDataInitializer implements ApplicationRunner {

  private final DevMemberDataInitializer memberDataInitializer;
  private final DevFestivalDataInitializer festivalDataInitializer;

  private static final long RECRUITER_COUNT = 3;
  private static final long JOB_SEEKER_COUNT = 10;
  private static final long JOB_EQUIP_COUNT = 5;
  private static final long TARGET_FESTIVAL_COUNT = 6;


  @Override
  public void run(@NonNull ApplicationArguments arg) throws Exception {

    memberDataInitializer.generateSampleMembers(RECRUITER_COUNT, JOB_SEEKER_COUNT, JOB_EQUIP_COUNT);
    festivalDataInitializer.generateSampleFestivals(TARGET_FESTIVAL_COUNT);

  }
}