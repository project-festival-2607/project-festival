package com.example.chook.support.service;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.AdminBoard;
import com.example.chook.support.entity.AdminBoardFile;
import com.example.chook.support.repository.AdminBoardFileRepository;
import com.example.chook.support.repository.AdminBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminBoardServiceImpl implements AdminBoardService {

  // 본문 마크다운 안에 박혀있는 "/adminBoard/image/{uuid}" 링크로 첨부 이미지를 역추적
  private static final Pattern IMAGE_UUID_PATTERN =
    Pattern.compile("/adminBoard/image/([0-9a-fA-F\\-]{36})");

  private final AdminBoardRepository adminBoardRepository;
  private final AdminBoardFileRepository adminBoardFileRepository;
  private final UploadedFileRepository uploadedFileRepository;

  @Transactional
  @Override
  public AdminBoard register(AdminBoardDTO dto) {

    AdminBoard savedBoard = adminBoardRepository.save(
      AdminBoard.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .build()
    );

    for (UUID uuid : extractImageUuids(dto.getContent())) {
      UploadedFile uploadedFile = uploadedFileRepository.findById(uuid)
        .orElseThrow(() -> new IllegalStateException(
          String.format("본문에 참조된 이미지(%s)를 업로드 기록에서 찾을 수 없음", uuid)
        ));
      adminBoardFileRepository.save(
        AdminBoardFile.builder()
          .adminBoard(savedBoard)
          .uploadedFile(uploadedFile)
          .build()
      );
    }

    return savedBoard;
  }

  private List<UUID> extractImageUuids(String content) {
    if (content == null) return List.of();
    return IMAGE_UUID_PATTERN.matcher(content)
      .results()
      .map(result -> UUID.fromString(result.group(1)))
      .distinct()
      .toList();
  }

}