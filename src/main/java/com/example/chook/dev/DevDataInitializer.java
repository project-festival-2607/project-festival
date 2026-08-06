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

  @Override
  public void run(@NonNull ApplicationArguments arg) throws Exception {
    memberDataInitializer.addSampleMembers(3, 10, 5);
  }
}