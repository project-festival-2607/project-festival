package com.example.chook.admin.service;

import com.example.chook.admin.condition.board.InquirySearchCondition;
import com.example.chook.admin.dto.board.AdminInquiryTableDTO;
import org.springframework.data.domain.Page;

public interface AdminBoardService {

  Page<AdminInquiryTableDTO> getInquiryListPage(int pageIdx, int pageSize, InquirySearchCondition condition);

}
