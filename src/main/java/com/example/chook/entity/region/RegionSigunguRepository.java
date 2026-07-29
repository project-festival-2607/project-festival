package com.example.chook.entity.region;

import com.example.chook.entity.RegionSigungu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionSigunguRepository extends JpaRepository<RegionSigungu, String> {

  List<RegionSigungu> findBySido_SidoCode(String sidoCode, Sort sort);

}
