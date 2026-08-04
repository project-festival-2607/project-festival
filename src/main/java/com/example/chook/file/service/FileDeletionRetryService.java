package com.example.chook.file.service;

import com.example.chook.file.FileDeletionFailureRecorder;
import com.example.chook.file.record.FilePath;
import com.example.chook.file.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class FileDeletionRetryService {

  private final FileDeletionFailureRecorder failureRecorder;
  private final FileStorage fileStorage;

  @Scheduled(cron = "${file.retry.cron}")
  public synchronized void retryFileDeletion() {

    log.info("파일 재삭제 작업 시작");

    List<FilePath> records = failureRecorder.load();
    List<FilePath> remainingRecords =
      deleteFiles(records);
    failureRecorder.update(remainingRecords);

    log.info("파일 재삭제 작업 종료");

  }

  private List<FilePath> deleteFiles(List<FilePath> records) {

    List<FilePath> remainingRecords = new ArrayList<>();

    for (FilePath record : records) {
      if (!fileStorage.delete(
        record.relativePath(),
        record.storedName()
      )) remainingRecords.add(record);
    }

    return remainingRecords;

  }

}
