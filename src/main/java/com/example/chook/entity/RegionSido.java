package com.example.chook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "region_sido")
public class RegionSido {

  @Id
  @Column(name = "sido_code", columnDefinition = "char(2)", nullable = false)
  private String code;

  @Column(name = "sido_name", length = 20, nullable = false)
  private String name;

  @Column(name = "sido_short_name", length = 10, nullable = false)
  private String shortName;

}
