package com.example.chook.initializer;

import com.example.chook.member.service.MemberInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

  private final MemberInitService memberInitService;

  @Override
  public void run(String... args) throws Exception {
    memberInitService.addSampleMembers(3, 10, 5);
  }
}