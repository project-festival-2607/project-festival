package com.example.chook.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileDeletionFailureRecorder {

  private final FileSystemProperties properties;

  private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
    .setHeader()
    .setSkipHeaderRecord(true).get();

  public synchronized List<FilePathRecord> load() {

    Path absoluteSystemDir = Paths.get(properties.getSystemDir());
    Path deleteFailedLogPath = absoluteSystemDir.resolve(properties.getDeleteFailLogFile());

    List<FilePathRecord> filePathRecords = new ArrayList<>();

    if (Files.notExists(deleteFailedLogPath)) return new ArrayList<>();  // 기록 자체가 없는 경우

    try (CSVParser parser = CSVParser.parse(deleteFailedLogPath, StandardCharsets.UTF_8, CSV_FORMAT)) {
      for (CSVRecord record : parser) {
        filePathRecords.add(new FilePathRecord(
          record.get("relative_path"),
          record.get("stored_name")
        ));
      }
    } catch (IOException exception) {
      log.error("파일 삭제 실패 로그 읽기 실패", exception);
    }

    return filePathRecords;

  }

  public synchronized void update(List<FilePathRecord> failureRecords) {
    clear();
    for (FilePathRecord record : failureRecords) {
      recordDeleteFailure(record);
    }
  }

  private synchronized void clear() {

    Path absoluteSystemDir = Paths.get(properties.getSystemDir());
    Path deleteFailedLogPath = absoluteSystemDir.resolve(properties.getDeleteFailLogFile());

    if (Files.notExists(deleteFailedLogPath)) return;
    try {
      Files.delete(deleteFailedLogPath);
    } catch (IOException e) {
      throw new IllegalStateException("파일 삭제 로그 초기화 실패", e);
    }
  }

  public synchronized boolean recordDeleteFailure(FilePathRecord record) {

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
          printer.printRecord("relative_path", "stored_name");
        printer.printRecord(record.relativePath(), record.storedName());
        return true;
      }
    } catch (IOException exception) {
      log.error("파일 삭제 실패 로그 작성 실패", exception);
      return false;
    }
  }

}
