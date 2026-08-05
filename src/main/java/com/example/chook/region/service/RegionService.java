package com.example.chook.region.service;

import com.example.chook.region.dto.RegionDTO;
import com.example.chook.region.entity.RegionSido;
import com.example.chook.region.entity.RegionSigungu;

import java.util.List;

public interface RegionService {

  // default method: interface에서 호환성을 위해 규칙이나 로직을 정의할 때 사용

  default RegionDTO toDto(RegionSido sido){
    return RegionDTO.builder()
      .code(sido.getCode())
      .name(sido.getName())
      .shortName(sido.getShortName())
      .build();
  }

  default RegionDTO toDto(RegionSigungu sigungu){
    return RegionDTO.builder()
      .code(sigungu.getCode())
      .name(sigungu.getName())
      .shortName(sigungu.getName())
      .build();
  }

  List<RegionDTO> getSidoList();
  List<RegionDTO> getSigunguList(String sidoCode);

}
