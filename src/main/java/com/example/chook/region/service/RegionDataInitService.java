package com.example.chook.region.service;

import com.example.chook.region.entity.RegionSido;
import com.example.chook.region.entity.RegionSigungu;
import com.example.chook.region.repository.RegionSidoRepository;
import com.example.chook.region.repository.RegionSigunguRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RegionDataInitService {

  private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
    .setHeader()
    .setSkipHeaderRecord(true).get();
  private final RegionSidoRepository regionSidoRepository;
  private final RegionSigunguRepository regionSigunguRepository;

  @Transactional
  public void importData() {
    if (regionSidoRepository.count() > 0) return;  // 이미 데이터 추가가 완료된 경우
    importSido();
    importSigungu();
  }

  private void importSido() {
    Resource resource = new ClassPathResource("data/region_sido.csv");
    try (
      Reader reader = new InputStreamReader(resource.getInputStream());
      CSVParser parser = CSV_FORMAT.parse(reader)
    ) {

      List<RegionSido> sidoList = new ArrayList<>();

      for (CSVRecord record : parser) {
        String code = record.get("sido_code");
        String name = record.get("sido_name");
        String shortName = record.get("sido_short_name");
        sidoList.add(
          new RegionSido(code, name, shortName)
        );
      }
      regionSidoRepository.saveAll(sidoList);

    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private void importSigungu() {
    Resource resource = new ClassPathResource("data/region_sigungu.csv");
    try (
      Reader reader = new InputStreamReader(resource.getInputStream());
      CSVParser parser = CSV_FORMAT.parse(reader)
    ) {

      List<RegionSigungu> sigunguList = new ArrayList<>();

      for (CSVRecord record : parser) {
        String code = record.get("sigungu_code");
        String name = record.get("sigungu_name");
        RegionSido sido = regionSidoRepository.getReferenceById(record.get("sido_code"));
        sigunguList.add(
          new RegionSigungu(code, sido, name)
        );
      }
      regionSigunguRepository.saveAll(sigunguList);

    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }
}
