package com.example.chook.admin.repository;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.condition.board.NoticeSearchCondition;
import com.example.chook.admin.dto.board.AdminInquiryTableDTO;
import com.example.chook.admin.dto.board.AdminNoticeTableDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminBoardRepository {

  Page<AdminInquiryTableDTO> getInquiryListPage(Pageable pageable, InquirySearchCondition condition);

  Page<AdminNoticeTableDTO> getNoticeListPage(Pageable pageable, NoticeSearchCondition condition);

}
