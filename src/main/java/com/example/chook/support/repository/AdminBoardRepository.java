package com.example.chook.support.repository;

import com.example.chook.support.entity.AdminBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminBoardRepository extends JpaRepository<AdminBoard, Long>, AdminBoardRepositoryCustom {
}