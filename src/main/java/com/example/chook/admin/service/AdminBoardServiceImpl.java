package com.example.chook.admin.service;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.condition.board.NoticeSearchCondition;
import com.example.chook.admin.dto.board.InquiryTableDTO;
import com.example.chook.admin.dto.board.NoticeTableDTO;
import com.example.chook.admin.entity.enums.inquiry.InquiryReplyStatus;
import com.example.chook.admin.repository.AdminBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminBoardServiceImpl implements AdminBoardService {

  private final AdminBoardRepository adminBoardRepository;

  @Override
  public Page<InquiryTableDTO> getInquiryListPage(int pageIdx, int pageSize, InquirySearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<InquiryTableDTO> result = adminBoardRepository.getInquiryListPage(pageable, condition);
    result.forEach(dto -> {
      dto.setReplyStatus(
        (dto.getRepliedAt() == null) ? InquiryReplyStatus.NOT_REPLIED : InquiryReplyStatus.REPLIED
      );
    });
    return result;
  }

  @Override
  public Page<NoticeTableDTO> getNoticeListPage(int pageIdx, int pageSize, NoticeSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminBoardRepository.getNoticeListPage(pageable, condition);
  }

}
