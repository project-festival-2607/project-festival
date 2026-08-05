package com.example.chook.support.service;

import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.AdminBoard;
import org.springframework.data.domain.Page;

public interface AdminBoardService {

  AdminBoard register(AdminBoardDTO dto);

  Page<AdminBoardDTO> getList(int page, String searchType, String keyword);

  AdminBoardDTO getDetail(Long bno);

  AdminBoard modify(Long bno, AdminBoardDTO dto);

  void delete(Long bno);

}