package com.example.chook.region.controller;

import com.example.chook.region.dto.RegionDTO;
import com.example.chook.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/region/*")
public class RegionController {

  private final RegionService regionService;

  @GetMapping("/sido")
  public List<RegionDTO> getSidoList() {
    return regionService.getSidoList();
  }

  @GetMapping("/sigungu")
  public List<RegionDTO> getSigunguList(@RequestParam String sidoCode) {
    return regionService.getSigunguList(sidoCode);
  }

}
