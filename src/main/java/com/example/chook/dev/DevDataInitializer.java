package com.example.chook.initializer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

  private final MemberInitService memberInitService;

  @Override
    memberInitService.addSampleMembers(3, 10, 5);
  public void run(@NonNull ApplicationArguments arg) throws Exception {
  }
}