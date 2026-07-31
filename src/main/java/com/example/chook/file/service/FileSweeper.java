package com.example.chook.file.service;

import com.example.chook.file.FilePathRecord;
import com.example.chook.file.FileStorage;
import com.example.chook.file.FileSystemProperties;
import com.example.chook.file.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Stream;


@EnableScheduling
@Slf4j
@Service
@RequiredArgsConstructor
public class FileSweeper {

  private final FileSystemProperties properties;
  private final UploadedFileRepository uploadedFileRepository;
  private final FileStorage fileStorage;

  @Scheduled(cron = "${file.sweep.cron}")
  public synchronized void fileSweep() {

    log.info("파일 정리 시작");

    Set<FilePathRecord> filePathRecords = uploadedFileRepository.findAllFilePaths();

    Path absoluteUploadDir = Paths.get(properties.getUploadDir());
    Instant now = Instant.now();

    if (Files.notExists(absoluteUploadDir)) return;

    try (Stream<Path> paths = Files.walk(absoluteUploadDir).filter(Files::isRegularFile)) {
      paths.forEach(filePath -> {
        String relativePath = absoluteUploadDir.relativize(filePath.getParent())
          .toString().replace('\\', '/');
        String fileName = filePath.getFileName().toString();
        if (!filePathRecords.contains(new FilePathRecord(fileName, relativePath))) {
          try {
            log.info("\"{}\" 파일이 DB에 존재하지 않습니다.",
              absoluteUploadDir.relativize(filePath)
            );
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            Instant creationTime = attributes.creationTime().toInstant();
            Duration gracePeriod = properties.getSweep().getGracePeriod();
            Instant expirationTime = creationTime.plus(gracePeriod);

            // DB에 등록되지 않은 파일 중 유예 기간이 지난 것만 삭제
            if (now.isAfter(expirationTime)) {
              if (!fileStorage.delete(relativePath, fileName)) {
                log.error("파일 삭제 실패: {}", filePath);
              }
            } else {
              log.info("파일 생성 후 유예 기간({})이 지나지 않아 삭제하지 않습니다.", gracePeriod);
            }
          } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
          }
        }
      });
    } catch (IOException e) {
      log.error("파일 정리 실패", e);
    }

    log.info("파일 정리 종료");

  }

}
