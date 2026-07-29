package com.example.chook.entity.region;

import com.example.chook.entity.RegionSido;
import com.example.chook.entity.RegionSigungu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegionServiceImpl implements RegionService {

  private final RegionSidoRepository regionSidoRepository;
  private final RegionSigunguRepository regionSigunguRepository;

  @Override
  public List<RegionDTO> getSidoList() {
    List<RegionSido> sidoList = regionSidoRepository.findAll(
      Sort.by(Sort.Direction.ASC, "code")
    );
    return sidoList.stream().map(this::toDto).toList();
  }

  @Override
  public List<RegionDTO> getSigunguList(String sidoCode) {
    List<RegionSigungu> sigunguList = regionSigunguRepository.findBySido_Code(
      sidoCode,
      Sort.by(Sort.Direction.ASC, "name")
    );
    return sigunguList.stream().map(this::toDto).toList();
  }
}
