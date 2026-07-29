package com.example.chook.entity.region;

import com.example.chook.entity.RegionSido;
import com.example.chook.entity.RegionSigungu;

import java.util.List;

public interface RegionService {

  // default method: interface에서 호환성을 위해 규칙이나 로직을 정의할 때 사용

  default RegionDTO toDto(RegionSido sido){
    return RegionDTO.builder()
      .code(sido.getSidoCode())
      .name(sido.getSidoName())
      .build();
  }

  default RegionDTO toDto(RegionSigungu sigungu){
    return RegionDTO.builder()
      .code(sigungu.getSigunguCode())
      .name(sigungu.getSigunguName())
      .build();
  }

  List<RegionDTO> getSidoList();
  List<RegionDTO> getSigunguList(String sidoCode);

}
