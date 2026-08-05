package com.example.chook.support.repository;

import com.example.chook.support.entity.AdminBoard;
import com.example.chook.support.entity.AdminBoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdminBoardFileRepository extends JpaRepository<AdminBoardFile, Long> {

  List<AdminBoardFile> findByAdminBoard(AdminBoard board);

  boolean existsByUploadedFile_Uuid(UUID uuid);
}