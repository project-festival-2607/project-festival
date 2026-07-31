package com.example.chook.file.service;

import com.example.chook.file.FileDeletionFailureRecord;
import com.example.chook.file.FileDeletionFailureRecorder;
import com.example.chook.file.FileStorage;
import com.example.chook.file.FileSystemProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDeletionRetryService {

  private final FileSystemProperties properties;
  private final FileDeletionFailureRecorder failureRecorder;
  private final FileStorage fileStorage;

  private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
    .setHeader()
    .setSkipHeaderRecord(true).get();

  @Scheduled(cron = "00 */10 * * * *")
  public synchronized void retryFileDeletion() {

    log.info("파일 재삭제 작업 시작");
    Path absoluteSystemDir = Paths.get(properties.getSystemDir());
    Path deleteFailedLogPath = absoluteSystemDir.resolve(properties.getDeleteFailLogFile());

    List<FileDeletionFailureRecord> deleteFailedRecords = loadDeleteFailedRecord(deleteFailedLogPath);
    List<FileDeletionFailureRecord> newDeleteFailedRecords =
      deleteFilesFromFailedRecords(deleteFailedRecords);
    writeDeleteFailedRecord(newDeleteFailedRecords);

    log.info("파일 재삭제 작업 종료");

  }

  private List<FileDeletionFailureRecord> loadDeleteFailedRecord(Path path) {

    List<FileDeletionFailureRecord> deleteFailedRecords = new ArrayList<>();

    if (Files.notExists(path)) return new ArrayList<>();  // 기록 자체가 없는 경우

    try (CSVParser parser = CSVParser.parse(path, StandardCharsets.UTF_8, CSV_FORMAT)) {
      for (CSVRecord record : parser) {
        deleteFailedRecords.add(new FileDeletionFailureRecord(
          record.get("uuid_str"),
          record.get("relative_path"),
          record.get("stored_name")
        ));
      }
    } catch (IOException exception) {
      log.error("파일 삭제 실패 로그 읽기 실패", exception);
    }

    return deleteFailedRecords;
  }

  private List<FileDeletionFailureRecord> deleteFilesFromFailedRecords(List<FileDeletionFailureRecord> deleteFailedRecords) {

    List<FileDeletionFailureRecord> newDeleteFailedRecords = new ArrayList<>();

    for (FileDeletionFailureRecord deleteFailedRecord : deleteFailedRecords) {
      if (!fileStorage.delete(
        deleteFailedRecord.relativePath(),
        deleteFailedRecord.storedName()
      )) newDeleteFailedRecords.add(deleteFailedRecord);
    }

    return newDeleteFailedRecords;

  }

  private void writeDeleteFailedRecord(List<FileDeletionFailureRecord> newDeleteFailedRecords) {

    failureRecorder.clear();
    for (FileDeletionFailureRecord deleteFailedRecord : newDeleteFailedRecords) {
      failureRecorder.recordDeleteFailure(deleteFailedRecord);
    }

  }

}
