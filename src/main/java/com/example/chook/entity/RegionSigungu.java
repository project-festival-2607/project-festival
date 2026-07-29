package com.example.chook.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "region_sigungu")
public class RegionSigungu {

  @Id
  @Column(name = "sigungu_code", columnDefinition = "char(5)", nullable = false)
  private String sigunguCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sido_code", nullable = false)
  private RegionSido sido;

  @Column(name = "sigungu_name", length = 20, nullable = false)
  private String sigunguName;

}
