package com.example.chook.region;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionDTO {

  private String code;
  private String name;
  private String shortName;

}
