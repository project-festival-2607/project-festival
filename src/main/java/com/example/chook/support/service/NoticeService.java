package com.example.chook.support.service;

import com.example.chook.support.dto.NoticeDTO;
import com.example.chook.support.entity.Notice;
import org.springframework.data.domain.Page;

public interface NoticeService {

  Notice register(NoticeDTO dto);

  Page<NoticeDTO> getList(int page, String searchType, String keyword);

  NoticeDTO getDetail(Long bno);

  Notice modify(Long bno, NoticeDTO dto);

  void delete(Long bno);

}