package com.example.chook.admin.record;

import lombok.Builder;

@Builder
public record AdminActionResponse(
  Boolean result,
  String message
) {
}
