package com.example.chook.file;

import com.example.chook.file.record.FilePath;
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

  private final FileProperties properties;

  private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
    .setHeader()
    .setSkipHeaderRecord(true).get();

  private Path getDeletionFailureLogPath() {
    Path systemDir = Paths.get(properties.getSystemDir());
    return systemDir.resolve(properties.getDeletionFailureLogFile());
  }

  public synchronized List<FilePath> load() {

    Path deletionFailureLogPath = getDeletionFailureLogPath();

    List<FilePath> filePathRecords = new ArrayList<>();

    if (Files.notExists(deletionFailureLogPath)) return new ArrayList<>();  // 기록 자체가 없는 경우

    try (CSVParser parser = CSVParser.parse(deletionFailureLogPath, StandardCharsets.UTF_8, CSV_FORMAT)) {
      for (CSVRecord record : parser) {
        filePathRecords.add(new FilePath(
          record.get("relative_path"),
          record.get("stored_name")
        ));
      }
    } catch (IOException exception) {
      log.error("파일 삭제 실패 로그 읽기 실패", exception);
    }

    return filePathRecords;

  }

  public synchronized void update(List<FilePath> failureRecords) {
    clear();
    for (FilePath record : failureRecords) {
      recordDeletionFailure(record);
    }
  }

  private synchronized void clear() {

    Path deletionFailureLogPath = getDeletionFailureLogPath();

    if (Files.notExists(deletionFailureLogPath)) return;
    try {
      Files.delete(deletionFailureLogPath);
    } catch (IOException e) {
      throw new IllegalStateException("파일 삭제 로그 초기화 실패", e);
    }
  }

  public synchronized boolean recordDeletionFailure(FilePath record) {

    Path deletionFailureLogPath = getDeletionFailureLogPath();

    try {
      Files.createDirectories(deletionFailureLogPath.getParent());
      boolean newFile = Files.notExists(deletionFailureLogPath);
      try (
        BufferedWriter writer = Files.newBufferedWriter(
          deletionFailureLogPath,
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
