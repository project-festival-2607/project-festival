package com.example.chook.support.service;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.Notice;
import com.example.chook.support.entity.NoticeFile;
import com.example.chook.support.repository.NoticeFileRepository;
import com.example.chook.support.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

  private static final int PAGE_SIZE = 10;

  // 본문 마크다운 안에 박혀있는 "/notice/image/{uuid}" 링크로 첨부 이미지를 역추적
  private static final Pattern IMAGE_UUID_PATTERN =
    Pattern.compile("/notice/image/([0-9a-fA-F\\-]{36})");

  private final NoticeRepository noticeRepository;
  private final NoticeFileRepository noticeFileRepository;
  private final UploadedFileRepository uploadedFileRepository;

  @Transactional
  @Override
  public Notice register(AdminBoardDTO dto) {

    Notice savedBoard = noticeRepository.save(
      Notice.builder()
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
      noticeFileRepository.save(
        NoticeFile.builder()
          .notice(savedBoard)
          .uploadedFile(uploadedFile)
          .build()
      );
    }

    return savedBoard;
  }

  @Override
  public Page<AdminBoardDTO> getList(int page, String searchType, String keyword) {
    // 정렬(하이라이트 우선, 최신순)은 검색 쿼리 안에서 처리
    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), PAGE_SIZE);
    Page<AdminBoardDTO> boardPage = noticeRepository.search(searchType, keyword, pageable).map(this::toDto);

    int seq = 1;
    for (AdminBoardDTO board : boardPage.getContent()) {
      board.setDisplayNo(Boolean.TRUE.equals(board.getHighlight()) ? "중요" : String.valueOf(seq++));
    }

    return boardPage;
  }

  @Override
  public AdminBoardDTO getDetail(Long bno) {
    Notice board = noticeRepository.findById(bno)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없음: " + bno));
    return toDto(board);
  }

  @Transactional
  @Override
  public Notice modify(Long bno, AdminBoardDTO dto) {
    Notice board = noticeRepository.findById(bno)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없음: " + bno));

    board.setTitle(dto.getTitle());
    board.setContent(dto.getContent());
    board.setHighlight(Boolean.TRUE.equals(dto.getHighlight()));

    // 이미 연결된 이미지는 건너뛰고, 수정하면서 새로 추가된 이미지만 연결
    for (UUID uuid : extractImageUuids(dto.getContent())) {
      if (noticeFileRepository.existsByUploadedFile_Uuid(uuid)) continue;
      UploadedFile uploadedFile = uploadedFileRepository.findById(uuid)
        .orElseThrow(() -> new IllegalStateException(
          String.format("본문에 참조된 이미지(%s)를 업로드 기록에서 찾을 수 없음", uuid)
        ));
      noticeFileRepository.save(
        NoticeFile.builder()
          .notice(board)
          .uploadedFile(uploadedFile)
          .build()
      );
    }

    return board;
  }

  @Transactional
  @Override
  public void delete(Long bno) {
    Notice board = noticeRepository.findById(bno)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없음: " + bno));
    // FK 제약 때문에 게시글보다 먼저 연결 레코드 삭제 (물리 파일은 FileSweeper가 추후 정리)
    noticeFileRepository.deleteAll(noticeFileRepository.findByNotice(board));
    noticeRepository.delete(board);
  }

  private AdminBoardDTO toDto(Notice board) {
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