package com.example.chook.file.record;

import org.springframework.core.io.Resource;

public record FileResource(
  Resource resource,
  String mimeType,
  String originalName
) {
}
