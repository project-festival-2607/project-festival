package com.example.chook.admin.record;

import lombok.Builder;

@Builder
public record BusinessRegistrationRequest(
  String businessNumber,
  String businessNumberVerify
) {
}
