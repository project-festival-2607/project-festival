package com.example.chook.file.service;

public record FileDeleteFailureRecord(
  String uuidStr,
  String relativePath,
  String storedName
) {
}
