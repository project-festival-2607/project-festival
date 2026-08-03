package com.example.chook.file;

public record FileDeletionFailureRecord(
  String uuidStr,
  String relativePath,
  String storedName
) {
}
