package com.example.chook.support.repository;

import com.example.chook.support.entity.AdminBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminBoardRepositoryCustom {

  Page<AdminBoard> search(String searchType, String keyword, Pageable pageable);

}