package com.example.chook.admin.service;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.condition.board.NoticeSearchCondition;
import com.example.chook.admin.dto.board.InquiryTableDTO;
import com.example.chook.admin.dto.board.NoticeTableDTO;
import org.springframework.data.domain.Page;

public interface AdminBoardService {

  Page<InquiryTableDTO> getInquiryListPage(int pageIdx, int pageSize, InquirySearchCondition condition);

  Page<NoticeTableDTO> getNoticeListPage(int pageIdx, int pageSize, NoticeSearchCondition condition);

}
