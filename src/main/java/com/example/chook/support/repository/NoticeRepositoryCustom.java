package com.example.chook.support.repository;

import com.example.chook.support.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {

  Page<Notice> search(String searchType, String keyword, Pageable pageable);

}