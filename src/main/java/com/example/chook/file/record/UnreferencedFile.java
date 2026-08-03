package com.example.chook.file.record;

import java.time.LocalDateTime;
import java.util.UUID;

public record UnreferencedFile(
  UUID uuid,
  LocalDateTime uploadedAt
) {
}
