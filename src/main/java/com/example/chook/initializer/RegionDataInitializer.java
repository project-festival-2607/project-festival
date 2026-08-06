package com.example.chook.initializer;

import com.example.chook.region.service.RegionDataInitService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionDataInitializer implements ApplicationRunner {

  private final RegionDataInitService regionDataInitService;

  @Override
  public void run(@NonNull ApplicationArguments args) throws Exception {
    regionDataInitService.importData();
  }
}
