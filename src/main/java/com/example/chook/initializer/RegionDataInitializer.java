package com.example.chook.initializer;

import com.example.chook.region.service.RegionDataInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionDataInitializer implements CommandLineRunner {

  private final RegionDataInitService regionDataInitService;

  @Override
  public void run(String... args) throws Exception {
    regionDataInitService.importData();
  }
}
