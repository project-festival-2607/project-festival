package com.example.chook.admin.dto.event;

import lombok.Builder;

@Builder
public record RegionOption(

  String name,
  String label

) {
}
