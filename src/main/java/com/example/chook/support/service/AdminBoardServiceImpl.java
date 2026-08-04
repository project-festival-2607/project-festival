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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminBoardServiceImpl implements AdminBoardService {

  private static final int PAGE_SIZE = 10;

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
        .highlight(Boolean.TRUE.equals(dto.getHighlight()))
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

  @Override
  public Page<AdminBoardDTO> getList(int page) {
    // 하이라이트 글을 항상 위로, 그 안에서는 최신순
    Pageable pageable = PageRequest.of(
      Math.max(page - 1, 0),
      PAGE_SIZE,
      Sort.by(Sort.Order.desc("highlight"), Sort.Order.desc("bno"))
    );
    Page<AdminBoardDTO> boardPage = adminBoardRepository.findAll(pageable).map(this::toDto);

    int seq = 1;
    for (AdminBoardDTO board : boardPage.getContent()) {
      board.setDisplayNo(Boolean.TRUE.equals(board.getHighlight()) ? "중요" : String.valueOf(seq++));
    }

    return boardPage;
  }

  @Override
  public AdminBoardDTO getDetail(Long bno) {
    AdminBoard board = adminBoardRepository.findById(bno)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없음: " + bno));
    return toDto(board);
  }

  private AdminBoardDTO toDto(AdminBoard board) {
    return AdminBoardDTO.builder()
      .bno(board.getBno())
      .title(board.getTitle())
      .content(board.getContent())
      .createdAt(board.getCreatedAt())
      .highlight(board.getHighlight())
      .build();
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