package com.example.chook.support.service;

import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import com.example.chook.file.service.FileService;
import com.example.chook.support.dto.InquiryDTO;
import com.example.chook.support.entity.Inquiry;
import com.example.chook.support.entity.InquiryFile;
import com.example.chook.support.repository.InquiryFileRepository;
import com.example.chook.support.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class InquiryServiceImpl implements InquiryService {

  // ponytail: 문의하기 첨부파일과 동일한 업로드 방식, 저장 경로만 inquiry 폴더로 고정
  private static final String FILE_RELATIVE_PATH = "inquiry";

  private final InquiryRepository inquiryRepository;
  private final InquiryFileRepository inquiryFileRepository;
  private final UploadedFileRepository uploadedFileRepository;
  private final FileService fileService;

  @Transactional
  @Override
  public Inquiry register(InquiryDTO dto, List<MultipartFile> files) {
    LocalDateTime now = LocalDateTime.now();

    Inquiry saved = inquiryRepository.save(
      Inquiry.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .createdAt(now)
        .updatedAt(now)
        .build()
    );

    if (files != null) {
      for (MultipartFile file : files) {
        if (file == null || file.isEmpty()) continue;

        FileDTO fileDto = fileService.uploadAndGetDto(file, FILE_RELATIVE_PATH);
        UploadedFile uploadedFile = uploadedFileRepository.getReferenceById(UUID.fromString(fileDto.getUuid()));

        inquiryFileRepository.save(
          InquiryFile.builder()
            .inquiry(saved)
            .uploadedFile(uploadedFile)
            .build()
        );
      }
    }

    return saved;
  }

  @Override
  public List<InquiryDTO> getList() {
    // ponytail: 로그인 미구현 - 지금은 전체 노출, 로그인 붙으면 본인 문의만 필터링
    return inquiryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
      .map(this::toDto)
      .toList();
  }

  @Override
  public List<InquiryDTO> getAdminList(String searchType, String keyword, String answered) {
    // 관리자 큐: 먼저 온 순서(오래된 순)로 처리하도록 정렬
    return inquiryRepository.search(searchType, keyword, answered).stream()
      .map(this::toDto)
      .toList();
  }

  @Override
  public InquiryDTO getDetail(Long ino) {
    Inquiry inquiry = inquiryRepository.findById(ino)
      .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없음: " + ino));
    return toDetailDto(inquiry);
  }

  @Transactional
  @Override
  public Inquiry answer(Long ino, String comment) {
    Inquiry inquiry = inquiryRepository.findById(ino)
      .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없음: " + ino));
    if (inquiry.getComment() != null) {
      throw new IllegalStateException("이미 답변이 등록된 문의는 수정할 수 없음: " + ino);
    }
    inquiry.setComment(comment);
    inquiry.setCommentTime(LocalDateTime.now());
    return inquiry;
  }

  private InquiryDTO toDto(Inquiry inquiry) {
    return InquiryDTO.builder()
      .ino(inquiry.getIno())
      .id(inquiry.getId())
      .title(inquiry.getTitle())
      .content(inquiry.getContent())
      .comment(inquiry.getComment())
      .createdAt(inquiry.getCreatedAt())
      .updatedAt(inquiry.getUpdatedAt())
      .commentTime(inquiry.getCommentTime())
      .build();
  }

  private InquiryDTO toDetailDto(Inquiry inquiry) {
    InquiryDTO dto = toDto(inquiry);
    dto.setFiles(
      inquiryFileRepository.findByInquiry(inquiry).stream()
        .map(InquiryFile::getUploadedFile)
        .map(fileService::toDto)
        .toList()
    );
    return dto;
  }

}