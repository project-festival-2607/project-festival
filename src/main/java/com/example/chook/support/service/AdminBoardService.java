package com.example.chook.support.service;

import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.AdminBoard;

public interface AdminBoardService {

  AdminBoard register(AdminBoardDTO dto);

}