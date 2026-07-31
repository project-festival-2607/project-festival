package com.example.chook.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileDeletionFailureRecorder {

  private final FileSystemProperties properties;

  public synchronized void clear() {

    Path absoluteSystemDir = Paths.get(properties.getSystemDir());
    Path deleteFailedLogPath = absoluteSystemDir.resolve(properties.getDeleteFailLogFile());

    if (Files.notExists(deleteFailedLogPath)) return;
    try {
      Files.delete(deleteFailedLogPath);
    } catch (IOException e) {
      throw new IllegalStateException("파일 삭제 로그 초기화 실패", e);
    }
  }

  public synchronized boolean recordDeleteFailure(FileDeletionFailureRecord record) {

    Path absoluteSystemDir = Paths.get(properties.getSystemDir());
    Path deleteFailedLogPath = absoluteSystemDir.resolve(properties.getDeleteFailLogFile());

    try {
      Files.createDirectories(absoluteSystemDir);
      boolean newFile = Files.notExists(deleteFailedLogPath);
      try (
        BufferedWriter writer = Files.newBufferedWriter(
          deleteFailedLogPath,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
      ) {
        if (newFile)
          printer.printRecord("uuid_str", "relative_path", "stored_name");
        printer.printRecord(record.uuidStr(), record.relativePath(), record.storedName());
        return true;
      }
    } catch (IOException exception) {
      log.error("파일 삭제 실패 로그 작성 실패", exception);
      return false;
    }
  }

}
