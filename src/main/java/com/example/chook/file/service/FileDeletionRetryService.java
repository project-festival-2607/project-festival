package com.example.chook.file.service;

import com.example.chook.file.FileDeletionFailureRecorder;
import com.example.chook.file.FilePathRecord;
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

    List<FilePathRecord> deleteFailedRecords = failureRecorder.load();
    List<FilePathRecord> newDeleteFailedRecords =
      deleteFilesFromFailedRecords(deleteFailedRecords);
    failureRecorder.update(newDeleteFailedRecords);

    log.info("파일 재삭제 작업 종료");

  }

  private List<FilePathRecord> deleteFilesFromFailedRecords(List<FilePathRecord> deleteFailedRecords) {

    List<FilePathRecord> newDeleteFailedRecords = new ArrayList<>();

    for (FilePathRecord deleteFailedRecord : deleteFailedRecords) {
      if (!fileStorage.delete(
        deleteFailedRecord.relativePath(),
        deleteFailedRecord.storedName()
      )) newDeleteFailedRecords.add(deleteFailedRecord);
    }

    return newDeleteFailedRecords;

  }

}
