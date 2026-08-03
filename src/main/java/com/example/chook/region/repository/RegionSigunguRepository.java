package com.example.chook.region.repository;

import com.example.chook.entity.RegionSigungu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionSigunguRepository extends JpaRepository<RegionSigungu, String> {

  List<RegionSigungu> findBySido_Code(String sidoCode, Sort sort);

}
