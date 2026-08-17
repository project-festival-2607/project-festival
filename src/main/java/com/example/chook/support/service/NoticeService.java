package com.example.chook.support.service;

import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.Notice;
import org.springframework.data.domain.Page;

public interface NoticeService {

  Notice register(AdminBoardDTO dto);

  Page<AdminBoardDTO> getList(int page, String searchType, String keyword);

  AdminBoardDTO getDetail(Long bno);

  Notice modify(Long bno, AdminBoardDTO dto);

  void delete(Long bno);

}