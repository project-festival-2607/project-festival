package com.example.chook.admin.repository;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.condition.board.NoticeSearchCondition;
import com.example.chook.admin.dto.board.InquiryTableDTO;
import com.example.chook.admin.dto.board.NoticeTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminBoardRepository {

  Page<InquiryTableDTO> getInquiryListPage(Pageable pageable, InquirySearchCondition condition);

  Page<NoticeTableDTO> getNoticeListPage(Pageable pageable, NoticeSearchCondition condition);

}
